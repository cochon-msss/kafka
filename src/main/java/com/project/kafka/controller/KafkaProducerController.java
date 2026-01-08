package com.project.kafka.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.kafka.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class KafkaProducerController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/sending")
    public void create() {
        kafkaProducerService.create();
    }

}
