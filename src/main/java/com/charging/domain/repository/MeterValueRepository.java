package com.charging.domain.repository;

import com.charging.domain.entity.MeterValue;
import com.charging.domain.enums.MeasurandEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MeterValue 엔티티를 위한 Repository
 * JPA 기반 데이터 액세스 레이어
 */
@Repository
public interface MeterValueRepository extends JpaRepository<MeterValue, Long> {

    /**
     * 트랜잭션 ID로 미터 값 조회 (시간순 정렬)
     */
    List<MeterValue> findByTransactionIdFkOrderByTimestampAsc(Long transactionId);

    /**
     * 측정값 종류로 조회
     */
    List<MeterValue> findByMeasurand(MeasurandEnum measurand);

    /**
     * 트랜잭션 ID와 측정값 종류로 조회
     */
    List<MeterValue> findByTransactionIdFkAndMeasurand(Long transactionId, MeasurandEnum measurand);

    /**
     * 기간별 미터 값 조회
     */
    @Query("SELECT m FROM MeterValue m WHERE m.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY m.timestamp ASC")
    List<MeterValue> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 트랜잭션별 최신 미터 값 조회
     */
    @Query("SELECT m FROM MeterValue m WHERE m.transactionIdFk = :transactionId " +
           "ORDER BY m.timestamp DESC LIMIT 1")
    MeterValue findLatestByTransactionId(@Param("transactionId") Long transactionId);

    /**
     * 에너지 미터 값만 조회 (kWh)
     */
    @Query("SELECT m FROM MeterValue m WHERE m.measurand = 'ENERGY_ACTIVE_IMPORT_REGISTER' " +
           "AND m.transactionIdFk = :transactionId ORDER BY m.timestamp ASC")
    List<MeterValue> findEnergyValuesByTransactionId(@Param("transactionId") Long transactionId);
}
