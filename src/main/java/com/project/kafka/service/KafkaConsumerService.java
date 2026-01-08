package com.project.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaConsumerService {

    @KafkaListener(topics = "topic", groupId = "group_2")
    public void listen(String message, Acknowledgment ack) {
        log.info("=====================================");
        log.info(">>> 컨슈머 메시지 수신: " + message);

        // 테스트
        if (message.contains("error")) {
            log.error("-> 에러 발생 재시도 시작");
            throw new RuntimeException("처리 중 에러 발생!");
        }
        // 정상 처리 시 ack 호출
        // commit은 카프카 브로커에세 여기까지 읽었다라는 오프셋 정보를 저장
        // ack.acknowledge();

        log.info(">>> 메시지 처리 완료");
        log.info("=====================================");
    }

    // DLQ에 쌓인 메시지를 모니터 리스너
    @KafkaListener(topics = "topic-dlt", groupId = "group_dlq", properties = { "auto.offset.reset=earliest" })
    public void listenDLQ(String message) {
        log.info("=====================================");
        log.info("==== DLQ 메시지 수신 성공!! : " + message + " ====");
        log.info("=====================================");
    }

}
