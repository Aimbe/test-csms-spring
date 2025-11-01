package com.charging.dto.response;

import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 트랜잭션 응답 DTO
 * 엔티티를 직접 노출하지 않고 필요한 정보만 전달
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    /**
     * 트랜잭션 ID
     */
    private String transactionId;

    /**
     * EVSE ID
     */
    private Integer evseId;

    /**
     * 충전소 ID
     */
    private String stationId;

    /**
     * 커넥터 ID
     */
    private Integer connectorId;

    /**
     * 인증 토큰
     */
    private String idToken;

    /**
     * 이벤트 타입
     */
    private TransactionEventEnum eventType;

    /**
     * 충전 상태
     */
    private ChargingStateEnum chargingState;

    /**
     * 시작 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 종료 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stopTime;

    /**
     * 누적 에너지 (kWh)
     */
    private BigDecimal totalEnergy;

    /**
     * 종료 이유
     */
    private String stopReason;

    /**
     * 생성 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
