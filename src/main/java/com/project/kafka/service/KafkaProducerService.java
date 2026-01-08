package com.project.kafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void create(String message) {
        try {
            kafkaTemplate.send("topic", message).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info(">>> [성공] 카프카 응답 완료! 오프셋: " + result.getRecordMetadata().offset());
                } else {
                    log.info(">>> [실패] 카프카 응답 에러: " + ex.getMessage());
                }
            });
            log.info(">>> [2] send() 메서드 호출 완료 (비동기 처리 중)");
        } catch (Exception e) {
            log.error(">>> [예외 발생] 전송 중 오류: " + e.getMessage());
        }
    }

}
