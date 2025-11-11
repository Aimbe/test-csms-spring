package com.charging.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka 토픽 설정 클래스
 * <p>
 * OCPP 충전 도메인에서 사용할 Kafka 토픽을 정의합니다.
 * 각 토픽은 충전 이벤트의 종류별로 분리되어 관리됩니다.
 * </p>
 *
 * @author Jay Park
 * @version 1.0.0
 * @since 2025-11-07
 */
@Configuration
public class KafkaConfig {

    /**
     * 토픽 이름 상수 정의
     */
    public static final String TRANSACTION_STARTED_TOPIC = "transaction.started";
    public static final String TRANSACTION_STOPPED_TOPIC = "transaction.stopped";
    public static final String TRANSACTION_UPDATED_TOPIC = "transaction.updated";
    public static final String CHARGING_STATE_CHANGED_TOPIC = "charging.state.changed";

    /**
     * 트랜잭션 시작 이벤트 토픽
     * <p>
     * 충전 트랜잭션이 시작될 때 이벤트를 발행하는 토픽입니다.
     * </p>
     *
     * @return NewTopic 트랜잭션 시작 토픽
     */
    @Bean
    public NewTopic transactionStartedTopic() {
        return TopicBuilder.name(TRANSACTION_STARTED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * 트랜잭션 종료 이벤트 토픽
     * <p>
     * 충전 트랜잭션이 종료될 때 이벤트를 발행하는 토픽입니다.
     * </p>
     *
     * @return NewTopic 트랜잭션 종료 토픽
     */
    @Bean
    public NewTopic transactionStoppedTopic() {
        return TopicBuilder.name(TRANSACTION_STOPPED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * 트랜잭션 업데이트 이벤트 토픽
     * <p>
     * 충전 트랜잭션이 업데이트될 때 이벤트를 발행하는 토픽입니다.
     * </p>
     *
     * @return NewTopic 트랜잭션 업데이트 토픽
     */
    @Bean
    public NewTopic transactionUpdatedTopic() {
        return TopicBuilder.name(TRANSACTION_UPDATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * 충전 상태 변경 이벤트 토픽
     * <p>
     * 충전 상태가 변경될 때 이벤트를 발행하는 토픽입니다.
     * </p>
     *
     * @return NewTopic 충전 상태 변경 토픽
     */
    @Bean
    public NewTopic chargingStateChangedTopic() {
        return TopicBuilder.name(CHARGING_STATE_CHANGED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
