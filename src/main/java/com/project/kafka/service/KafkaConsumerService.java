package com.project.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {

    @KafkaListener(topics = "topic", groupId = "group_2")
    public void listen(String message) {
        System.out.println("=====================================");
        System.out.println(">>> 컨슈머 메시지 수신: " + message);

        // 테스트
        if (message.contains("error")) {
            System.out.println("-> 에러 발생 재시도 시작");
            throw new RuntimeException("처리 중 에러 발생!");
        }
        System.out.println(">>> 메시지 처리 완료");
        System.out.println("=====================================");
    }

    // DLQ에 쌓인 메시지를 모니터 리스너
    @KafkaListener(topics = "topic-dlt", groupId = "group_dlq", properties = { "auto.offset.reset=earliest" })
    public void listenDLQ(String message) {
        System.out.println("=====================================");
        System.out.println("==== DLQ 메시지 수신 성공!! : " + message + " ====");
        System.out.println("=====================================");
    }

}
