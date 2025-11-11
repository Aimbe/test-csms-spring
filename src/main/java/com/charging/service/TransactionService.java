package com.charging.service;

import com.charging.common.constants.TransactionConstants;
import com.charging.common.mapper.TransactionMapper;
import com.charging.domain.entity.Evse;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.charging.domain.repository.EvseRepository;
import com.charging.domain.repository.TransactionRepository;
import com.charging.dto.event.ChargingStateChangedEvent;
import com.charging.dto.event.TransactionStartedEvent;
import com.charging.dto.event.TransactionStoppedEvent;
import com.charging.dto.request.TransactionStartRequest;
import com.charging.dto.response.TransactionResponse;
import com.charging.exception.ResourceNotFoundException;
import com.charging.kafka.producer.TransactionEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 트랜잭션 관리 서비스
 * OCPP 2.0.1 트랜잭션 비즈니스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final EvseRepository evseRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionEventProducer eventProducer;

    /**
     * 트랜잭션 시작
     */
    @Transactional
    public TransactionResponse startTransaction(TransactionStartRequest request) {
        log.info("트랜잭션 시작 요청: evseId={}, stationId={}, connectorId={}, idToken={}",
                request.getEvseId(), request.getStationId(), request.getConnectorId(), request.getIdToken());

        // EVSE 존재 여부 확인
        Evse evse = evseRepository.findByEvseIdAndStationId(request.getEvseId(), request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EVSE",
                        "evseId-stationId",
                        request.getEvseId() + "-" + request.getStationId()));

        // 트랜잭션 ID 생성
        String transactionId = generateTransactionId();

        // 트랜잭션 엔티티 생성
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .evseId(request.getEvseId())
                .stationId(request.getStationId())
                .connectorId(request.getConnectorId())
                .idToken(request.getIdToken())
                .eventType(TransactionEventEnum.STARTED)
                .chargingState(ChargingStateEnum.IDLE)
                .startTime(LocalDateTime.now())
                .build();

        // 양방향 관계 설정
        evse.addTransaction(transaction);

        // 저장
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("트랜잭션 시작 완료: transactionId={}", transactionId);

        // Kafka 이벤트 발행
        publishTransactionStartedEvent(savedTransaction);

        // DTO 변환 후 반환
        return transactionMapper.toResponse(savedTransaction);
    }

    /**
     * 트랜잭션 종료
     */
    @Transactional
    public TransactionResponse stopTransaction(String transactionId, String stopReason) {
        log.info("트랜잭션 종료 요청: transactionId={}, stopReason={}", transactionId, stopReason);

        // 트랜잭션 조회
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionId", transactionId));

        // 종료 이유 기본값 설정
        String finalStopReason = StringUtils.hasText(stopReason)
                ? stopReason
                : TransactionConstants.DEFAULT_STOP_REASON;

        // 트랜잭션 종료 처리
        transaction.stop(LocalDateTime.now(), finalStopReason);
        transaction.calculateTotalEnergy();

        // 저장
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("트랜잭션 종료 완료: transactionId={}, totalEnergy={} kWh",
                transactionId, transaction.getTotalEnergy());

        // Kafka 이벤트 발행
        publishTransactionStoppedEvent(savedTransaction, finalStopReason);

        // DTO 변환 후 반환
        return transactionMapper.toResponse(savedTransaction);
    }

    /**
     * 충전 상태 업데이트
     */
    @Transactional
    public TransactionResponse updateChargingState(String transactionId, ChargingStateEnum newState) {
        log.info("충전 상태 업데이트: transactionId={}, newState={}", transactionId, newState);

        // 트랜잭션 조회
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionId", transactionId));

        // 이전 상태 저장
        ChargingStateEnum previousState = transaction.getChargingState();

        // 상태 업데이트
        transaction.updateChargingState(newState);

        // 저장
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Kafka 이벤트 발행
        publishChargingStateChangedEvent(savedTransaction, previousState, newState);

        // DTO 변환 후 반환
        return transactionMapper.toResponse(savedTransaction);
    }

    /**
     * 활성 트랜잭션 조회
     */
    public List<TransactionResponse> getActiveTransactions(String stationId) {
        List<Transaction> transactions = transactionRepository.findActiveTransactions(stationId);
        return transactionMapper.toResponseList(transactions);
    }

    /**
     * 트랜잭션 ID 생성
     */
    private String generateTransactionId() {
        return TransactionConstants.TRANSACTION_ID_PREFIX + System.currentTimeMillis();
    }

    /**
     * 트랜잭션 시작 이벤트 발행
     *
     * @param transaction 시작된 트랜잭션
     */
    private void publishTransactionStartedEvent(Transaction transaction) {
        TransactionStartedEvent event = TransactionStartedEvent.builder()
                .transactionId(transaction.getTransactionId())
                .evseId(transaction.getEvseId())
                .stationId(transaction.getStationId())
                .connectorId(transaction.getConnectorId())
                .idToken(transaction.getIdToken())
                .chargingState(transaction.getChargingState())
                .startTime(transaction.getStartTime())
                .eventTime(LocalDateTime.now())
                .eventDescription("충전 트랜잭션이 시작되었습니다.")
                .build();

        eventProducer.publishTransactionStarted(event);
    }

    /**
     * 트랜잭션 종료 이벤트 발행
     *
     * @param transaction 종료된 트랜잭션
     * @param stopReason  종료 사유
     */
    private void publishTransactionStoppedEvent(Transaction transaction, String stopReason) {
        TransactionStoppedEvent event = TransactionStoppedEvent.builder()
                .transactionId(transaction.getTransactionId())
                .evseId(transaction.getEvseId())
                .stationId(transaction.getStationId())
                .connectorId(transaction.getConnectorId())
                .chargingState(transaction.getChargingState())
                .startTime(transaction.getStartTime())
                .stopTime(transaction.getStopTime())
                .totalEnergy(transaction.getTotalEnergy())
                .stopReason(stopReason)
                .eventTime(LocalDateTime.now())
                .eventDescription("충전 트랜잭션이 종료되었습니다.")
                .build();

        eventProducer.publishTransactionStopped(event);
    }

    /**
     * 충전 상태 변경 이벤트 발행
     *
     * @param transaction   트랜잭션
     * @param previousState 이전 상태
     * @param currentState  현재 상태
     */
    private void publishChargingStateChangedEvent(Transaction transaction,
                                                    ChargingStateEnum previousState,
                                                    ChargingStateEnum currentState) {
        ChargingStateChangedEvent event = ChargingStateChangedEvent.builder()
                .transactionId(transaction.getTransactionId())
                .evseId(transaction.getEvseId())
                .stationId(transaction.getStationId())
                .connectorId(transaction.getConnectorId())
                .previousState(previousState)
                .currentState(currentState)
                .stateChangedTime(LocalDateTime.now())
                .eventTime(LocalDateTime.now())
                .eventDescription(String.format("충전 상태가 %s에서 %s로 변경되었습니다.",
                        previousState, currentState))
                .build();

        eventProducer.publishChargingStateChanged(event);
    }
}
