package com.charging.controller;

import com.charging.dto.request.ChargingStateUpdateRequest;
import com.charging.dto.request.TransactionStartRequest;
import com.charging.dto.request.TransactionStopRequest;
import com.charging.dto.response.ApiResponse;
import com.charging.dto.response.TransactionResponse;
import com.charging.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 트랜잭션 REST API Controller
 * OCPP 2.0.1 트랜잭션 관리 API
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 트랜잭션 시작
     *
     * POST /api/transactions/start
     *
     * @param request 트랜잭션 시작 요청 정보
     * @return 생성된 트랜잭션 정보
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<TransactionResponse>> startTransaction(
            @Valid @RequestBody TransactionStartRequest request) {

        log.info("트랜잭션 시작 API 호출: {}", request);

        TransactionResponse response = transactionService.startTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("트랜잭션이 성공적으로 시작되었습니다", response));
    }

    /**
     * 트랜잭션 종료
     *
     * POST /api/transactions/{transactionId}/stop
     *
     * @param transactionId 트랜잭션 ID
     * @param request 종료 요청 정보 (종료 이유 포함)
     * @return 종료된 트랜잭션 정보
     */
    @PostMapping("/{transactionId}/stop")
    public ResponseEntity<ApiResponse<TransactionResponse>> stopTransaction(
            @PathVariable String transactionId,
            @RequestBody(required = false) TransactionStopRequest request) {

        log.info("트랜잭션 종료 API 호출: transactionId={}", transactionId);

        String stopReason = (request != null && request.getStopReason() != null)
                ? request.getStopReason()
                : null;

        TransactionResponse response = transactionService.stopTransaction(transactionId, stopReason);

        return ResponseEntity.ok(
                ApiResponse.success("트랜잭션이 성공적으로 종료되었습니다", response));
    }

    /**
     * 충전 상태 업데이트
     *
     * PATCH /api/transactions/{transactionId}/charging-state
     *
     * @param transactionId 트랜잭션 ID
     * @param request 충전 상태 업데이트 요청
     * @return 업데이트된 트랜잭션 정보
     */
    @PatchMapping("/{transactionId}/charging-state")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateChargingState(
            @PathVariable String transactionId,
            @Valid @RequestBody ChargingStateUpdateRequest request) {

        log.info("충전 상태 업데이트 API 호출: transactionId={}, chargingState={}",
                transactionId, request.getChargingState());

        TransactionResponse response = transactionService.updateChargingState(
                transactionId, request.getChargingState());

        return ResponseEntity.ok(
                ApiResponse.success("충전 상태가 성공적으로 업데이트되었습니다", response));
    }

    /**
     * 활성 트랜잭션 조회
     *
     * GET /api/transactions/active
     *
     * @param stationId 충전소 ID
     * @return 활성 트랜잭션 목록
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getActiveTransactions(
            @RequestParam String stationId) {

        log.info("활성 트랜잭션 조회 API 호출: stationId={}", stationId);

        List<TransactionResponse> transactions = transactionService.getActiveTransactions(stationId);

        return ResponseEntity.ok(
                ApiResponse.success("활성 트랜잭션 조회가 완료되었습니다", transactions));
    }
}
