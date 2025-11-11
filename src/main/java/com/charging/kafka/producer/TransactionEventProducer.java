package com.charging.kafka.producer;

import com.charging.config.KafkaConfig;
import com.charging.dto.event.ChargingStateChangedEvent;
import com.charging.dto.event.TransactionStartedEvent;
import com.charging.dto.event.TransactionStoppedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 트랜잭션 이벤트 Kafka Producer
 * <p>
 * 충전 트랜잭션과 관련된 이벤트를 Kafka에 발행합니다.
 * 각 이벤트는 적절한 토픽으로 전송되며, 비동기 방식으로 처리됩니다.
 * </p>
 *
 * @author Jay Park
 * @version 1.0.0
 * @since 2025-11-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 트랜잭션 시작 이벤트 발행
     * <p>
     * 충전 트랜잭션이 시작될 때 호출되며, 시작 이벤트를 Kafka로 전송합니다.
     * </p>
     *
     * @param event 트랜잭션 시작 이벤트 정보
     */
    public void publishTransactionStarted(TransactionStartedEvent event) {
        log.info("트랜잭션 시작 이벤트 발행 시작 - Transaction ID: {}", event.getTransactionId());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaConfig.TRANSACTION_STARTED_TOPIC, event.getTransactionId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("트랜잭션 시작 이벤트 발행 성공 - Transaction ID: {}, Offset: {}",
                    event.getTransactionId(),
                    result.getRecordMetadata().offset());
            } else {
                log.error("트랜잭션 시작 이벤트 발행 실패 - Transaction ID: {}",
                    event.getTransactionId(), ex);
            }
        });
    }

    /**
     * 트랜잭션 종료 이벤트 발행
     * <p>
     * 충전 트랜잭션이 종료될 때 호출되며, 종료 이벤트를 Kafka로 전송합니다.
     * </p>
     *
     * @param event 트랜잭션 종료 이벤트 정보
     */
    public void publishTransactionStopped(TransactionStoppedEvent event) {
        log.info("트랜잭션 종료 이벤트 발행 시작 - Transaction ID: {}", event.getTransactionId());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaConfig.TRANSACTION_STOPPED_TOPIC, event.getTransactionId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("트랜잭션 종료 이벤트 발행 성공 - Transaction ID: {}, Total Energy: {} kWh, Offset: {}",
                    event.getTransactionId(),
                    event.getTotalEnergy(),
                    result.getRecordMetadata().offset());
            } else {
                log.error("트랜잭션 종료 이벤트 발행 실패 - Transaction ID: {}",
                    event.getTransactionId(), ex);
            }
        });
    }

    /**
     * 충전 상태 변경 이벤트 발행
     * <p>
     * 충전 상태가 변경될 때 호출되며, 상태 변경 이벤트를 Kafka로 전송합니다.
     * </p>
     *
     * @param event 충전 상태 변경 이벤트 정보
     */
    public void publishChargingStateChanged(ChargingStateChangedEvent event) {
        log.info("충전 상태 변경 이벤트 발행 시작 - Transaction ID: {}, {} -> {}",
            event.getTransactionId(),
            event.getPreviousState(),
            event.getCurrentState());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaConfig.CHARGING_STATE_CHANGED_TOPIC, event.getTransactionId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("충전 상태 변경 이벤트 발행 성공 - Transaction ID: {}, Offset: {}",
                    event.getTransactionId(),
                    result.getRecordMetadata().offset());
            } else {
                log.error("충전 상태 변경 이벤트 발행 실패 - Transaction ID: {}",
                    event.getTransactionId(), ex);
            }
        });
    }
}
