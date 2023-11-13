package com.prometheus.ospegle.spring;

import com.prometheus.ospegle.pojo.Room;
import com.prometheus.ospegle.utils.RoomQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentLinkedDeque;

@Component("local")
public class LocalPeerQueue implements RoomQueue {

    private final Logger LOGGER = LoggerFactory.getLogger(LocalPeerQueue.class);
    private final ConcurrentLinkedDeque<Room> queue = new ConcurrentLinkedDeque<>();

    @Override
    public Mono<Room> getRoom(String userId) {
        return Mono.create(sink -> {
            // if rooms exist for the peer to join
            if (!queue.isEmpty()) {
                Room room = queue.pollFirst();
                room.getLatch().countDown();

                LOGGER.info(String.format("User %s joining existing room %s", userId, room.getUuid()));
                sink.success(room);
                return;
            }
            // create room and wait for other peer to join
            Room room = new Room();
            LOGGER.info(String.format("User %s creating new room %s", userId, room.getUuid()));
            queue.add(room);
            Mono.fromRunnable(() -> {
                try {
                    room.getLatch().await();
                    LOGGER.info(String.format("User %s joining room %s", userId, room.getUuid()));
                    sink.success(room);
                } catch (InterruptedException e) {
                    sink.error(e);
                }
            }).subscribe();
        });
    }

}
