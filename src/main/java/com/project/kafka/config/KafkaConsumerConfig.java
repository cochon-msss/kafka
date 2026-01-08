package com.project.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka // kafka 리스너 어노테이션 활성화, kafka 리스너가 spring 컨텍스트에서 작동하도록 한다.
public class KafkaConsumerConfig {

    // kafka Consumer를 생성하는 팩토리
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        // ConsumerFactory 구현체를 반환하는 로직 작성
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_2");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 오프셋을 찾을 수 없을 때 가장 최신의 메시지부터 읽기 시작하도록 설정
        // 이 컨슈머 그룹 group_1 이 처음 생성되었을 때 기존에 쌓여있던 메시지는 무시하고 지금 이 수간부터 들어오는 메시지만 받겠단 뜻
        // 과거 데이터부터 다 훑어야 한다면 earliest로 변경
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // 토픽에 대해서 auto commit으로 100초로 간격을 설정
        // 만약 메시지를 처리하고 50초 뒤에 서버가 갑자기 꺼지면 카프카는 마지막으로 커밋된 지점(100초전)부터 다시 메시지를 보낸다 중복처리
        // 발생 가능성 존재
        // 따라서 실무에서는 auto commit을 false로 설정하고 수동 커밋을 사용하는 것이 일반적이다.
        // config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100000");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    // kafka 리스너 컨테이너 팩토리 정의, kafka 리스너를 컨테이너화하여 실행
    // 네트워크 순단이나 DB 락 등으로 인해 컨슈머가 일시적으로 실패할 수 있다
    // 이때 포기하지 않고 재시도를 한 뒤 그래도 안 되면 별도의 쓰레기통(DLQ, Dead Letter Queue) 토픽으로 보내서 나중에 원인
    // 분석 진행해야한다.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // 에러 핸들러 추가

        // 재시도 실패 시 메시지를 보낼 DLQ 설정 (실패한 토픽명.DLQ로 전송됨)
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        // 재시도 정책 : 1초 관격으로 최대 3번 재시도
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);

        // 에러 핸들러 등록
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // 특정 예외는 재시도 하지 않도록 설정 가능 (옵션)
        // errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

}
