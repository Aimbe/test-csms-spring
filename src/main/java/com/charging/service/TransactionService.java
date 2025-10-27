package com.charging.service;

import com.charging.domain.entity.Evse;
import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.charging.domain.repository.EvseRepository;
import com.charging.domain.repository.TransactionRepository;
import com.charging.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 트랜잭션 시작
     */
    @Transactional
    public Transaction startTransaction(Integer evseId, String stationId, Integer connectorId, String idToken) {
        log.info("트랜잭션 시작 요청: evseId={}, stationId={}, connectorId={}, idToken={}",
                evseId, stationId, connectorId, idToken);

        Evse evse = evseRepository.findByEvseIdAndStationId(evseId, stationId)
                .orElseThrow(() -> new ResourceNotFoundException("EVSE", "evseId-stationId",
                        evseId + "-" + stationId));

        String transactionId = generateTransactionId();

        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .evseId(evseId)
                .stationId(stationId)
                .connectorId(connectorId)
                .idToken(idToken)
                .eventType(TransactionEventEnum.STARTED)
                .chargingState(ChargingStateEnum.IDLE)
                .startTime(LocalDateTime.now())
                .build();

        evse.addTransaction(transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("트랜잭션 시작 완료: transactionId={}", transactionId);

        return savedTransaction;
    }

    /**
     * 트랜잭션 종료
     */
    @Transactional
    public Transaction stopTransaction(String transactionId, String stopReason) {
        log.info("트랜잭션 종료 요청: transactionId={}, stopReason={}", transactionId, stopReason);

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionId", transactionId));

        transaction.stop(LocalDateTime.now(), stopReason);
        transaction.calculateTotalEnergy();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("트랜잭션 종료 완료: transactionId={}, totalEnergy={} kWh",
                transactionId, transaction.getTotalEnergy());

        return savedTransaction;
    }

    /**
     * 충전 상태 업데이트
     */
    @Transactional
    public Transaction updateChargingState(String transactionId, ChargingStateEnum newState) {
        log.info("충전 상태 업데이트: transactionId={}, newState={}", transactionId, newState);

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionId", transactionId));

        transaction.updateChargingState(newState);

        return transactionRepository.save(transaction);
    }

    /**
     * 활성 트랜잭션 조회
     */
    public List<Transaction> getActiveTransactions(String stationId) {
        return transactionRepository.findActiveTransactions(stationId);
    }

    /**
     * 트랜잭션 ID 생성 (간단한 구현)
     */
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }
}
