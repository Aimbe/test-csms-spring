package com.charging.domain.repository;

import com.charging.domain.entity.Connector;
import com.charging.domain.enums.ConnectorStatusEnum;
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
     */
    Optional<Connector> findByEvseIdAndStationIdAndConnectorId(
        Integer evseId,
        String stationId,
        Integer connectorId
    );

    /**
     * EVSE ID와 충전소 ID로 모든 커넥터 조회
     */
    List<Connector> findByEvseIdAndStationId(Integer evseId, String stationId);

    /**
     * 충전소 ID로 모든 커넥터 조회
     */
    List<Connector> findByStationId(String stationId);

    /**
     * 상태로 커넥터 조회
     */
    List<Connector> findByStatus(ConnectorStatusEnum status);

    /**
     * 충전소 ID와 상태로 커넥터 조회
     */
    List<Connector> findByStationIdAndStatus(String stationId, ConnectorStatusEnum status);

    /**
     * 특정 전력량 이상의 커넥터 조회
     */
    @Query("SELECT c FROM Connector c WHERE c.maxPower >= :minPower AND c.stationId = :stationId")
    List<Connector> findByMinPowerGreaterThanEqualAndStationId(
        @Param("minPower") java.math.BigDecimal minPower,
        @Param("stationId") String stationId
    );

    /**
     * 사용 가능한 커넥터 조회
     */
    @Query("SELECT c FROM Connector c WHERE c.status = 'AVAILABLE' AND c.stationId = :stationId")
    List<Connector> findAvailableConnectors(@Param("stationId") String stationId);
}
