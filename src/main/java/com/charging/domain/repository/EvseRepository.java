package com.charging.domain.repository;

import com.charging.domain.entity.Evse;
import com.charging.domain.enums.OperationalStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EVSE 엔티티를 위한 Repository
 * JPA 기반 데이터 액세스 레이어
 */
@Repository
public interface EvseRepository extends JpaRepository<Evse, Long> {

    /**
     * EVSE ID와 충전소 ID로 EVSE 조회 (복합 유니크 키)
     */
    Optional<Evse> findByEvseIdAndStationId(Integer evseId, String stationId);

    /**
     * 충전소 ID로 모든 EVSE 조회
     */
    List<Evse> findByStationId(String stationId);

    /**
     * EVSE와 연관된 모든 커넥터를 함께 조회 (N+1 문제 해결)
     */
    @Query("SELECT e FROM Evse e LEFT JOIN FETCH e.connectors " +
           "WHERE e.evseId = :evseId AND e.stationId = :stationId")
    Optional<Evse> findByEvseIdAndStationIdWithConnectors(
        @Param("evseId") Integer evseId,
        @Param("stationId") String stationId
    );

    /**
     * 충전소의 모든 EVSE와 커넥터를 함께 조회
     */
    @Query("SELECT DISTINCT e FROM Evse e LEFT JOIN FETCH e.connectors WHERE e.stationId = :stationId")
    List<Evse> findByStationIdWithConnectors(@Param("stationId") String stationId);

    /**
     * 운영 상태로 EVSE 조회
     */
    List<Evse> findByOperationalStatus(OperationalStatusEnum status);

    /**
     * 충전소 ID와 운영 상태로 EVSE 조회
     */
    List<Evse> findByStationIdAndOperationalStatus(String stationId, OperationalStatusEnum status);
}
