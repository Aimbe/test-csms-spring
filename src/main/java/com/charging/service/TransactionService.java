package com.charging.service;

import com.charging.common.constants.TransactionConstants;
import com.charging.common.mapper.TransactionMapper;
import com.charging.domain.entity.Evse;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.charging.domain.repository.EvseRepository;
import com.charging.domain.repository.TransactionRepository;
import com.charging.dto.request.TransactionStartRequest;
import com.charging.dto.response.TransactionResponse;
import com.charging.exception.ResourceNotFoundException;
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

        // 상태 업데이트
        transaction.updateChargingState(newState);

        // 저장
        Transaction savedTransaction = transactionRepository.save(transaction);

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
}
