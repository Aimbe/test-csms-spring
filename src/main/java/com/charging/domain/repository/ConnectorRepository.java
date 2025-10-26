package com.charging.domain.repository;

import com.charging.domain.entity.Connector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Connector 엔티티를 위한 Repository
 * JPA 기반 데이터 액세스 레이어
 */
@Repository
public interface ConnectorRepository extends JpaRepository<Connector, Long> {

    /**
     * 복합 유니크 키로 커넥터 조회
     * @param chargePointId 충전기 ID
     * @param stationId 충전소 ID
     * @param connectorId 커넥터 ID
     * @return Optional<Connector>
     */
    Optional<Connector> findByChargePointIdAndStationIdAndConnectorId(
        String chargePointId,
        String stationId,
        Integer connectorId
    );

    /**
     * 충전기 ID와 충전소 ID로 모든 커넥터 조회
     * @param chargePointId 충전기 ID
     * @param stationId 충전소 ID
     * @return List<Connector>
     */
    List<Connector> findByChargePointIdAndStationId(String chargePointId, String stationId);

    /**
     * 충전소 ID로 모든 커넥터 조회
     * @param stationId 충전소 ID
     * @return List<Connector>
     */
    List<Connector> findByStationId(String stationId);

    /**
     * 특정 전력량 이상의 커넥터 조회
     * @param minPower 최소 전력량
     * @param stationId 충전소 ID
     * @return List<Connector>
     */
    @Query("SELECT c FROM Connector c WHERE c.maxPower >= :minPower AND c.stationId = :stationId")
    List<Connector> findByMinPowerGreaterThanEqualAndStationId(
        @Param("minPower") java.math.BigDecimal minPower,
        @Param("stationId") String stationId
    );
}
