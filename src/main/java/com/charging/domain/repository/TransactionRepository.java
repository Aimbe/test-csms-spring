package com.charging.domain.repository;

import com.charging.domain.entity.Transaction;
import com.charging.domain.enums.ChargingStateEnum;
import com.charging.domain.enums.TransactionEventEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Transaction 엔티티를 위한 Repository
 * JPA 기반 데이터 액세스 레이어
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 트랜잭션 ID로 조회
     */
    Optional<Transaction> findByTransactionId(String transactionId);

    /**
     * 충전소 ID로 트랜잭션 조회
     */
    List<Transaction> findByStationId(String stationId);

    /**
     * EVSE ID와 충전소 ID로 트랜잭션 조회
     */
    List<Transaction> findByEvseIdAndStationId(Integer evseId, String stationId);

    /**
     * ID Token으로 트랜잭션 조회
     */
    List<Transaction> findByIdToken(String idToken);

    /**
     * 이벤트 유형으로 트랜잭션 조회
     */
    List<Transaction> findByEventType(TransactionEventEnum eventType);

    /**
     * 충전 상태로 트랜잭션 조회
     */
    List<Transaction> findByChargingState(ChargingStateEnum chargingState);

    /**
     * 진행 중인 트랜잭션 조회 (종료되지 않은 것)
     */
    @Query("SELECT t FROM Transaction t WHERE t.stopTime IS NULL AND t.stationId = :stationId")
    List<Transaction> findActiveTransactions(@Param("stationId") String stationId);

    /**
     * 트랜잭션과 미터 값을 함께 조회 (N+1 문제 해결)
     */
    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.meterValues WHERE t.transactionId = :transactionId")
    Optional<Transaction> findByTransactionIdWithMeterValues(@Param("transactionId") String transactionId);

    /**
     * 기간별 트랜잭션 조회
     */
    @Query("SELECT t FROM Transaction t WHERE t.startTime BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 충전소별 기간별 트랜잭션 조회
     */
    @Query("SELECT t FROM Transaction t WHERE t.stationId = :stationId " +
           "AND t.startTime BETWEEN :startDate AND :endDate")
    List<Transaction> findByStationIdAndDateRange(
        @Param("stationId") String stationId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
