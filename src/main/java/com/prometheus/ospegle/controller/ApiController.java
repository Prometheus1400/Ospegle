package com.prometheus.ospegle.controller;

import com.prometheus.ospegle.pojo.Room;
import com.prometheus.ospegle.utils.RoomQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.prometheus.ospegle.utils.Constants.API_PREFIX;

@RestController
@RequestMapping(API_PREFIX)
public class ApiController {
    @Autowired
    @Qualifier("local")
    private RoomQueue queue;

    // gets a room for calling user (guaranteed associated with at least another user)
    // user provides their own uuid to this method (generated from client side code)
    @GetMapping("/get-connection/{uuid}")
    public Mono<Room> getConnection(@PathVariable final String uuid) {
        // TODO add logic for using PeerQueue
        return queue.getRoom(uuid);
    }
}