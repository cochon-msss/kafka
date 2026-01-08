package com.project.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {

    @KafkaListener(topics = "topic", groupId = "group_1")
    public void listen(String message) {
        System.out.println("Received Message: " + message);
    }

}
