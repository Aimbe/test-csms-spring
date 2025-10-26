package com.charging.domain.repository;

import com.charging.domain.entity.ChargePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ChargePoint 엔티티를 위한 Repository
 * JPA 기반 데이터 액세스 레이어
 */
@Repository
public interface ChargePointRepository extends JpaRepository<ChargePoint, Long> {

    /**
     * 충전기 ID와 충전소 ID로 충전기 조회 (복합 유니크 키)
     * @param chargePointId 충전기 ID
     * @param stationId 충전소 ID
     * @return Optional<ChargePoint>
     */
    Optional<ChargePoint> findByChargePointIdAndStationId(String chargePointId, String stationId);

    /**
     * 충전소 ID로 모든 충전기 조회
     * @param stationId 충전소 ID
     * @return List<ChargePoint>
     */
    List<ChargePoint> findByStationId(String stationId);

    /**
     * 충전기와 연관된 모든 커넥터를 함께 조회 (N+1 문제 해결)
     * @param chargePointId 충전기 ID
     * @param stationId 충전소 ID
     * @return Optional<ChargePoint>
     */
    @Query("SELECT cp FROM ChargePoint cp LEFT JOIN FETCH cp.connectors " +
           "WHERE cp.chargePointId = :chargePointId AND cp.stationId = :stationId")
    Optional<ChargePoint> findByChargePointIdAndStationIdWithConnectors(
        @Param("chargePointId") String chargePointId,
        @Param("stationId") String stationId
    );

    /**
     * 충전소의 모든 충전기와 커넥터를 함께 조회
     * @param stationId 충전소 ID
     * @return List<ChargePoint>
     */
    @Query("SELECT DISTINCT cp FROM ChargePoint cp LEFT JOIN FETCH cp.connectors WHERE cp.stationId = :stationId")
    List<ChargePoint> findByStationIdWithConnectors(@Param("stationId") String stationId);
}
