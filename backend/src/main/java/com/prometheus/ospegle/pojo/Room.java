package com.prometheus.ospegle.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Data
public class Room implements Serializable {
    private String uuid = UUID.randomUUID().toString();
    @JsonIgnore
    private transient CountDownLatch latch = new CountDownLatch(1);
}
