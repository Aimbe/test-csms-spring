package com.charging.controller;

import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 트랜잭션 REST API Controller
 * OCPP 2.0.1 트랜잭션 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 트랜잭션 시작
     *
     * POST /api/transactions/start
     */
    @PostMapping("/start")
    public ResponseEntity<Transaction> startTransaction(
            @RequestParam Integer evseId,
            @RequestParam String stationId,
            @RequestParam Integer connectorId,
            @RequestParam String idToken) {

        Transaction transaction = transactionService.startTransaction(
                evseId, stationId, connectorId, idToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    /**
     * 트랜잭션 종료
     *
     * POST /api/transactions/{transactionId}/stop
     */
    @PostMapping("/{transactionId}/stop")
    public ResponseEntity<Transaction> stopTransaction(
            @PathVariable String transactionId,
            @RequestParam(required = false, defaultValue = "Normal") String stopReason) {

        Transaction transaction = transactionService.stopTransaction(transactionId, stopReason);

        return ResponseEntity.ok(transaction);
    }

    /**
     * 충전 상태 업데이트
     *
     * PATCH /api/transactions/{transactionId}/charging-state
     */
    @PatchMapping("/{transactionId}/charging-state")
    public ResponseEntity<Transaction> updateChargingState(
            @PathVariable String transactionId,
            @RequestParam ChargingStateEnum chargingState) {

        Transaction transaction = transactionService.updateChargingState(transactionId, chargingState);

        return ResponseEntity.ok(transaction);
    }

    /**
     * 활성 트랜잭션 조회
     *
     * GET /api/transactions/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Transaction>> getActiveTransactions(
            @RequestParam String stationId) {

        List<Transaction> transactions = transactionService.getActiveTransactions(stationId);

        return ResponseEntity.ok(transactions);
    }
}
