package com.prometheus.ospegle.spring;

import com.prometheus.ospegle.pojo.Room;
import com.prometheus.ospegle.utils.RoomQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component("local")
public class LocalPeerQueue implements RoomQueue {

    private final Logger LOGGER = LoggerFactory.getLogger(LocalPeerQueue.class);
    private final ConcurrentLinkedDeque<Room> queue = new ConcurrentLinkedDeque<>();
    @Value("${request.timeout}")
    private Long timeout;

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
            Mono.defer(() -> {
                try {
                    boolean result = room.getLatch().await(timeout, TimeUnit.SECONDS);  // Blocking call
                    if (!result) {
                        LOGGER.info("Timeout waiting for join room");
                        queue.remove(room);
                        throw new TimeoutException("Took too long waiting for peer to join room");
                    }
                    sink.success(room);
                } catch (Exception e) {
                    sink.error(e);
                }
                return Mono.empty();
            }).subscribeOn(Schedulers.boundedElastic()).subscribe();
        });
    }

}
