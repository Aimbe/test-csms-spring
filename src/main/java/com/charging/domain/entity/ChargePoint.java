package com.charging.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 충전기 엔티티
 * OCPP 2.0 기반 충전기 정보를 관리합니다.
 */
@Entity
@Table(
    name = "CHARGE_POINT",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "idx_charge_point_unique",
            columnNames = {"charge_point_id", "station_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChargePoint extends BaseEntity {

    /**
     * ID (Primary Key)
     * Oracle IDENTITY 컬럼으로 자동 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 충전기 ID
     * station_id와 함께 복합 유니크 키 구성
     */
    @Column(name = "charge_point_id", length = 50, nullable = false)
    private String chargePointId;

    /**
     * 충전소 ID (FK 역할 - 문자열)
     * @ManyToOne으로 연관관계 매핑
     */
    @Column(name = "station_id", length = 50, nullable = false, insertable = false, updatable = false)
    private String stationId;

    /**
     * 최대 허용 전력량 (kW)
     */
    @Column(name = "max_power", precision = 10, scale = 2, nullable = false)
    private BigDecimal maxPower;

    /**
     * 소속 충전소
     * N:1 관계 - 여러 충전기가 하나의 충전소에 속함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", referencedColumnName = "station_id", nullable = false)
    private Station station;

    /**
     * 충전기에 속한 커넥터 목록
     * 1:N 관계 - 하나의 충전기는 여러 커넥터를 가질 수 있음
     */
    @OneToMany(mappedBy = "chargePoint", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Connector> connectors = new ArrayList<>();

    /**
     * 충전소 설정 헬퍼 메서드
     * 양방향 관계를 위해 package-private으로 설정
     */
    void setStation(Station station) {
        this.station = station;
    }

    /**
     * 커넥터 추가 헬퍼 메서드
     * 양방향 관계를 편리하게 설정
     */
    public void addConnector(Connector connector) {
        connectors.add(connector);
        connector.setChargePoint(this);
    }

    /**
     * 커넥터 제거 헬퍼 메서드
     */
    public void removeConnector(Connector connector) {
        connectors.remove(connector);
        connector.setChargePoint(null);
    }
}
