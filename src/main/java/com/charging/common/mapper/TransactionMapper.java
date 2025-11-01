package com.charging.common.mapper;

import com.charging.domain.entity.Transaction;
import com.charging.dto.response.TransactionResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Transaction 엔티티 <-> DTO 매퍼
 */
@Component
public class TransactionMapper {

    /**
     * Entity -> Response DTO 변환
     */
    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .evseId(transaction.getEvseId())
                .stationId(transaction.getStationId())
                .connectorId(transaction.getConnectorId())
                .idToken(transaction.getIdToken())
                .eventType(transaction.getEventType())
                .chargingState(transaction.getChargingState())
                .startTime(transaction.getStartTime())
                .stopTime(transaction.getStopTime())
                .totalEnergy(transaction.getTotalEnergy())
                .stopReason(transaction.getStopReason())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    /**
     * Entity 리스트 -> Response DTO 리스트 변환
     */
    public List<TransactionResponse> toResponseList(List<Transaction> transactions) {
        if (transactions == null) {
            return List.of();
        }

        return transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
