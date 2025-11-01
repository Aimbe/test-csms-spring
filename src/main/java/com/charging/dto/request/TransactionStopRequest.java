package com.charging.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 트랜잭션 종료 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionStopRequest {

    /**
     * 종료 이유
     */
    private String stopReason;
}
