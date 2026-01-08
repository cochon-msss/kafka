package com.project.kafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void create(String message) {
        System.out.println(">>> 카프카로 메시지 전송 요청: " + message);
        try {
            kafkaTemplate.send("topic", message).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println(">>> [성공] 카프카 응답 완료! 오프셋: " + result.getRecordMetadata().offset());
                } else {
                    System.err.println(">>> [실패] 카프카 응답 에러: " + ex.getMessage());
                }
            });
            System.out.println(">>> [2] send() 메서드 호출 완료 (비동기 처리 중)");
        } catch (Exception e) {
            System.err.println(">>> [예외 발생] 전송 중 오류: " + e.getMessage());
        }
    }

}
