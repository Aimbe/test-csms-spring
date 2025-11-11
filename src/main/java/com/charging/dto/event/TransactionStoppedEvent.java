package com.charging.dto.event;

import com.charging.domain.enums.ChargingStateEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 트랜잭션 종료 이벤트 DTO
 * <p>
 * 충전 트랜잭션이 종료될 때 발행되는 이벤트 정보를 담고 있습니다.
 * Kafka 메시지로 전송되어 다른 시스템에서 구독할 수 있습니다.
 * </p>
 *
 * @author Jay Park
 * @version 1.0.0
 * @since 2025-11-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStoppedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 충전 상태
     */
    private ChargingStateEnum chargingState;

    /**
     * 트랜잭션 시작 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 트랜잭션 종료 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stopTime;

    /**
     * 총 충전 에너지 (kWh)
     */
    private BigDecimal totalEnergy;

    /**
     * 종료 사유
     */
    private String stopReason;

    /**
     * 이벤트 발행 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTime;

    /**
     * 이벤트 설명
     */
    private String eventDescription;
}
