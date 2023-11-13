package com.prometheus.ospegle.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class MainController {
    @Value("${app.version}")
    private String version;

    @GetMapping("/version")
    public Mono<String> getVersion() {
        return Mono.just(String.format("running version %s of Ospegle", version));
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ServerResponse> getHealth() {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(Map.of("status", "ok"));
    }
}
