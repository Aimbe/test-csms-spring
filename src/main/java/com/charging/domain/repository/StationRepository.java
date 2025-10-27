package com.charging.domain.repository;

import com.charging.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Station 엔티티를 위한 Repository
 * JPA 기반 데이터 액세스 레이어
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    /**
     * 충전소 ID로 충전소 조회
     * @param stationId 충전소 ID
     * @return Optional<Station>
     */
    Optional<Station> findByStationId(String stationId);

    /**
     * 충전소 ID로 충전소 존재 여부 확인
     * @param stationId 충전소 ID
     * @return 존재 여부
     */
    boolean existsByStationId(String stationId);

    /**
     * 충전소와 연관된 모든 EVSE를 함께 조회 (N+1 문제 해결)
     * @param stationId 충전소 ID
     * @return Optional<Station>
     */
    @Query("SELECT s FROM Station s LEFT JOIN FETCH s.evses WHERE s.stationId = :stationId")
    Optional<Station> findByStationIdWithEvses(@Param("stationId") String stationId);
}
