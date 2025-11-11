package com.charging.kafka.consumer;

import com.charging.config.KafkaConfig;
import com.charging.dto.event.ChargingStateChangedEvent;
import com.charging.dto.event.TransactionStartedEvent;
import com.charging.dto.event.TransactionStoppedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 트랜잭션 이벤트 Kafka Consumer
 * <p>
 * Kafka에서 발행된 충전 트랜잭션 이벤트를 구독하고 처리합니다.
 * 각 이벤트 타입별로 별도의 리스너 메서드가 존재하며,
 * 이벤트 수신 후 필요한 비즈니스 로직을 수행합니다.
 * </p>
 *
 * @author Jay Park
 * @version 1.0.0
 * @since 2025-11-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    /**
     * 트랜잭션 시작 이벤트 수신 및 처리
     * <p>
     * 충전 트랜잭션 시작 이벤트를 구독하여 처리합니다.
     * 이벤트 수신 후 알림, 로깅, 통계 집계 등의 추가 작업을 수행할 수 있습니다.
     * </p>
     *
     * @param event         트랜잭션 시작 이벤트
     * @param partition     파티션 번호
     * @param offset        오프셋
     * @param acknowledgment 수동 ACK 처리를 위한 객체
     */
    @KafkaListener(
        topics = KafkaConfig.TRANSACTION_STARTED_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransactionStarted(
        @Payload TransactionStartedEvent event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        try {
            log.info("트랜잭션 시작 이벤트 수신 - Transaction ID: {}, Partition: {}, Offset: {}",
                event.getTransactionId(), partition, offset);

            // 비즈니스 로직 처리
            processTransactionStarted(event);

            // 수동으로 오프셋 커밋
            acknowledgment.acknowledge();

            log.info("트랜잭션 시작 이벤트 처리 완료 - Transaction ID: {}", event.getTransactionId());

        } catch (Exception e) {
            log.error("트랜잭션 시작 이벤트 처리 중 오류 발생 - Transaction ID: {}",
                event.getTransactionId(), e);
            // 재처리 또는 DLQ(Dead Letter Queue)로 전송하는 로직 추가 가능
        }
    }

    /**
     * 트랜잭션 종료 이벤트 수신 및 처리
     * <p>
     * 충전 트랜잭션 종료 이벤트를 구독하여 처리합니다.
     * 이벤트 수신 후 과금, 정산, 통계 집계 등의 작업을 수행할 수 있습니다.
     * </p>
     *
     * @param event         트랜잭션 종료 이벤트
     * @param partition     파티션 번호
     * @param offset        오프셋
     * @param acknowledgment 수동 ACK 처리를 위한 객체
     */
    @KafkaListener(
        topics = KafkaConfig.TRANSACTION_STOPPED_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransactionStopped(
        @Payload TransactionStoppedEvent event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        try {
            log.info("트랜잭션 종료 이벤트 수신 - Transaction ID: {}, Total Energy: {} kWh, Partition: {}, Offset: {}",
                event.getTransactionId(), event.getTotalEnergy(), partition, offset);

            // 비즈니스 로직 처리
            processTransactionStopped(event);

            // 수동으로 오프셋 커밋
            acknowledgment.acknowledge();

            log.info("트랜잭션 종료 이벤트 처리 완료 - Transaction ID: {}", event.getTransactionId());

        } catch (Exception e) {
            log.error("트랜잭션 종료 이벤트 처리 중 오류 발생 - Transaction ID: {}",
                event.getTransactionId(), e);
        }
    }

    /**
     * 충전 상태 변경 이벤트 수신 및 처리
     * <p>
     * 충전 상태 변경 이벤트를 구독하여 처리합니다.
     * 이벤트 수신 후 모니터링, 알림 등의 작업을 수행할 수 있습니다.
     * </p>
     *
     * @param event         충전 상태 변경 이벤트
     * @param partition     파티션 번호
     * @param offset        오프셋
     * @param acknowledgment 수동 ACK 처리를 위한 객체
     */
    @KafkaListener(
        topics = KafkaConfig.CHARGING_STATE_CHANGED_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeChargingStateChanged(
        @Payload ChargingStateChangedEvent event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        try {
            log.info("충전 상태 변경 이벤트 수신 - Transaction ID: {}, {} -> {}, Partition: {}, Offset: {}",
                event.getTransactionId(),
                event.getPreviousState(),
                event.getCurrentState(),
                partition,
                offset);

            // 비즈니스 로직 처리
            processChargingStateChanged(event);

            // 수동으로 오프셋 커밋
            acknowledgment.acknowledge();

            log.info("충전 상태 변경 이벤트 처리 완료 - Transaction ID: {}", event.getTransactionId());

        } catch (Exception e) {
            log.error("충전 상태 변경 이벤트 처리 중 오류 발생 - Transaction ID: {}",
                event.getTransactionId(), e);
        }
    }

    /**
     * 트랜잭션 시작 이벤트 비즈니스 로직 처리
     * <p>
     * 실제 비즈니스 로직은 여기에 구현합니다.
     * 예: 알림 발송, 통계 수집, 외부 시스템 연동 등
     * </p>
     *
     * @param event 트랜잭션 시작 이벤트
     */
    private void processTransactionStarted(TransactionStartedEvent event) {
        // TODO: 비즈니스 로직 구현
        // 예: 충전 시작 알림 발송
        // 예: 실시간 대시보드 업데이트
        // 예: 통계 데이터 수집
        log.debug("트랜잭션 시작 처리 - Station: {}, EVSE: {}, Connector: {}",
            event.getStationId(), event.getEvseId(), event.getConnectorId());
    }

    /**
     * 트랜잭션 종료 이벤트 비즈니스 로직 처리
     * <p>
     * 실제 비즈니스 로직은 여기에 구현합니다.
     * 예: 과금 처리, 정산 시스템 연동, 이용 내역 저장 등
     * </p>
     *
     * @param event 트랜잭션 종료 이벤트
     */
    private void processTransactionStopped(TransactionStoppedEvent event) {
        // TODO: 비즈니스 로직 구현
        // 예: 과금 처리
        // 예: 충전 완료 알림 발송
        // 예: 정산 시스템 연동
        log.debug("트랜잭션 종료 처리 - Station: {}, Total Energy: {} kWh, Stop Reason: {}",
            event.getStationId(), event.getTotalEnergy(), event.getStopReason());
    }

    /**
     * 충전 상태 변경 이벤트 비즈니스 로직 처리
     * <p>
     * 실제 비즈니스 로직은 여기에 구현합니다.
     * 예: 상태 모니터링, 이상 감지, 실시간 알림 등
     * </p>
     *
     * @param event 충전 상태 변경 이벤트
     */
    private void processChargingStateChanged(ChargingStateChangedEvent event) {
        // TODO: 비즈니스 로직 구현
        // 예: 상태 변경 모니터링
        // 예: 이상 상태 감지 및 알림
        // 예: 실시간 대시보드 업데이트
        log.debug("충전 상태 변경 처리 - Previous: {}, Current: {}",
            event.getPreviousState(), event.getCurrentState());
    }
}
