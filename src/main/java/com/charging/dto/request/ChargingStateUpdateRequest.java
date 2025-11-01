package com.charging.dto.request;

import com.charging.domain.enums.ChargingStateEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 충전 상태 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingStateUpdateRequest {

    /**
     * 새로운 충전 상태
     */
    @NotNull(message = "충전 상태는 필수입니다")
    private ChargingStateEnum chargingState;
}
