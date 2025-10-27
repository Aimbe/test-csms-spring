package com.charging.domain.repository;

import com.charging.domain.entity.ChargingProfile;
import com.charging.domain.enums.ChargingProfileKindEnum;
import com.charging.domain.enums.ChargingProfilePurposeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ChargingProfile 엔티티를 위한 Repository
 * JPA 기반 데이터 액세스 레이어
 */
@Repository
public interface ChargingProfileRepository extends JpaRepository<ChargingProfile, Long> {

    /**
     * 충전 프로파일 ID로 조회
     */
    Optional<ChargingProfile> findByChargingProfileId(Integer chargingProfileId);

    /**
     * 충전소 ID로 프로파일 조회
     */
    List<ChargingProfile> findByStationId(String stationId);

    /**
     * 충전소 ID와 EVSE ID로 프로파일 조회
     */
    List<ChargingProfile> findByStationIdAndEvseId(String stationId, Integer evseId);

    /**
     * 트랜잭션 ID로 프로파일 조회
     */
    List<ChargingProfile> findByTransactionId(String transactionId);

    /**
     * 프로파일 목적으로 조회
     */
    List<ChargingProfile> findByChargingProfilePurpose(ChargingProfilePurposeEnum purpose);

    /**
     * 프로파일 종류로 조회
     */
    List<ChargingProfile> findByChargingProfileKind(ChargingProfileKindEnum kind);

    /**
     * 활성화된 프로파일 조회
     */
    List<ChargingProfile> findByIsActiveTrue();

    /**
     * 충전소별 활성화된 프로파일 조회
     */
    List<ChargingProfile> findByStationIdAndIsActiveTrue(String stationId);

    /**
     * 현재 유효한 프로파일 조회
     */
    @Query("SELECT cp FROM ChargingProfile cp WHERE cp.isActive = true " +
           "AND (cp.validFrom IS NULL OR cp.validFrom <= :now) " +
           "AND (cp.validTo IS NULL OR cp.validTo >= :now) " +
           "AND cp.stationId = :stationId " +
           "ORDER BY cp.stackLevel DESC")
    List<ChargingProfile> findActiveProfilesByStationId(
        @Param("stationId") String stationId,
        @Param("now") LocalDateTime now
    );

    /**
     * 스택 레벨로 정렬된 프로파일 조회
     */
    @Query("SELECT cp FROM ChargingProfile cp WHERE cp.stationId = :stationId " +
           "AND cp.isActive = true ORDER BY cp.stackLevel DESC")
    List<ChargingProfile> findByStationIdOrderByStackLevelDesc(@Param("stationId") String stationId);
}
