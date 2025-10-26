package com.charging.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 커넥터 엔티티
 * OCPP 2.0 기반 커넥터 정보를 관리합니다.
 */
@Entity
@Table(
    name = "CONNECTOR",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "idx_connector_unique",
            columnNames = {"charge_point_id", "station_id", "connector_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Connector extends BaseEntity {

    /**
     * ID (Primary Key)
     * Oracle IDENTITY 컬럼으로 자동 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 충전기 ID (FK 역할 - 문자열)
     * @ManyToOne으로 연관관계 매핑
     */
    @Column(name = "charge_point_id", length = 50, nullable = false, insertable = false, updatable = false)
    private String chargePointId;

    /**
     * 충전소 ID (FK 역할 - 문자열)
     */
    @Column(name = "station_id", length = 50, nullable = false)
    private String stationId;

    /**
     * 커넥터 ID
     * charge_point_id, station_id와 함께 복합 유니크 키 구성
     */
    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;

    /**
     * 최대 허용 전력량 (kW)
     */
    @Column(name = "max_power", precision = 10, scale = 2, nullable = false)
    private BigDecimal maxPower;

    /**
     * 최소 허용 전력량 (kW)
     */
    @Column(name = "min_power", precision = 10, scale = 2, nullable = false)
    private BigDecimal minPower;

    /**
     * 소속 충전기
     * N:1 관계 - 여러 커넥터가 하나의 충전기에 속함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "charge_point_id", referencedColumnName = "charge_point_id", nullable = false),
        @JoinColumn(name = "station_id", referencedColumnName = "station_id", nullable = false)
    })
    private ChargePoint chargePoint;

    /**
     * 충전기 설정 헬퍼 메서드
     * 양방향 관계를 위해 package-private으로 설정
     */
    void setChargePoint(ChargePoint chargePoint) {
        this.chargePoint = chargePoint;
    }
}
