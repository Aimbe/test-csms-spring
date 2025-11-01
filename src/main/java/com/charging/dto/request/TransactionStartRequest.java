package com.charging.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 트랜잭션 시작 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionStartRequest {

    /**
     * EVSE ID
     */
    @NotNull(message = "EVSE ID는 필수입니다")
    @Min(value = 1, message = "EVSE ID는 1 이상이어야 합니다")
    private Integer evseId;

    /**
     * 충전소 ID
     */
    @NotBlank(message = "충전소 ID는 필수입니다")
    private String stationId;

    /**
     * 커넥터 ID
     */
    @NotNull(message = "커넥터 ID는 필수입니다")
    @Min(value = 1, message = "커넥터 ID는 1 이상이어야 합니다")
    private Integer connectorId;

    /**
     * 인증 토큰 (RFID, ISO15118 등)
     */
    @NotBlank(message = "인증 토큰은 필수입니다")
    private String idToken;
}
