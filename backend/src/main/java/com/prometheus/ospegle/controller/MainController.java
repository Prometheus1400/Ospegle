package com.prometheus.ospegle.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MainController {
    @Value("${app.version}")
    private String version;

    @GetMapping("/version")
    public Mono<String> getVersion() {
        return Mono.just(String.format("running version %s of Ospegle", version));
    }
}
