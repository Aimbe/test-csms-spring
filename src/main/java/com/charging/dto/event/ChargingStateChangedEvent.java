package com.charging.dto.event;

import com.charging.domain.enums.ChargingStateEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 충전 상태 변경 이벤트 DTO
 * <p>
 * 충전 상태가 변경될 때 발행되는 이벤트 정보를 담고 있습니다.
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
public class ChargingStateChangedEvent implements Serializable {

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
     * 이전 충전 상태
     */
    private ChargingStateEnum previousState;

    /**
     * 현재 충전 상태
     */
    private ChargingStateEnum currentState;

    /**
     * 상태 변경 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stateChangedTime;

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
