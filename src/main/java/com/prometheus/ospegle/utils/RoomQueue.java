package com.prometheus.ospegle.utils;

import com.prometheus.ospegle.pojo.Room;
import reactor.core.publisher.Mono;

public interface RoomQueue {
    // ideal flow is if no rooms are created it should create a room and wait for
    // another user to join that room before returning RoomDetails to both users
    Mono<Room> getRoom(final String userId);
}
