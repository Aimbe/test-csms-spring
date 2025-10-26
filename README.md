# OCPP 2.0 충전기 도메인 - JPA 학습 프로젝트

OCPP 2.0 기반 전기차 충전소 도메인을 JPA로 모델링한 학습 프로젝트입니다.

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/charging/
│   │   ├── ChargingDomainApplication.java    # Spring Boot 메인 클래스
│   │   └── domain/
│   │       ├── entity/                        # JPA 엔티티
│   │       │   ├── BaseEntity.java           # 공통 필드 (created_at, updated_at)
│   │       │   ├── Station.java              # 충전소 엔티티
│   │       │   ├── ChargePoint.java          # 충전기 엔티티
│   │       │   └── Connector.java            # 커넥터 엔티티
│   │       └── repository/                    # JPA Repository
│   │           ├── StationRepository.java
│   │           ├── ChargePointRepository.java
│   │           └── ConnectorRepository.java
│   └── resources/
│       ├── application.yml                    # 애플리케이션 설정
│       └── schema.sql                         # Oracle DDL 스크립트
├── build.gradle                               # Gradle 빌드 설정
├── settings.gradle                            # Gradle 설정
├── gradlew                                    # Gradle Wrapper (Unix/Linux/Mac)
└── gradlew.bat                                # Gradle Wrapper (Windows)
```

## 도메인 모델

### ERD

```
STATION (충전소)
    ↓ 1:N
CHARGE_POINT (충전기)
    ↓ 1:N
CONNECTOR (커넥터)
```

### 엔티티 설명

#### 1. Station (충전소)
- 전기차 충전소 정보를 관리
- 전력 수용량, 가격 정책, 스마트충전 알고리즘 설정 등을 포함
- 여러 개의 충전기(ChargePoint)를 소유

#### 2. ChargePoint (충전기)
- 개별 충전기 정보를 관리
- 하나의 충전소에 속하며, 여러 개의 커넥터를 가짐
- 복합 유니크 키: (charge_point_id, station_id)

#### 3. Connector (커넥터)
- 실제 충전 포트 정보를 관리
- 하나의 충전기에 속함
- 복합 유니크 키: (charge_point_id, station_id, connector_id)

## JPA 학습 포인트

### 1. 엔티티 매핑
- `@Entity`: 엔티티 클래스 선언
- `@Table`: 테이블 매핑 및 유니크 제약조건 설정
- `@Column`: 컬럼 속성 정의 (nullable, length, precision, scale)
- `@Id`, `@GeneratedValue`: 기본 키 및 자동 생성 전략

### 2. 연관관계 매핑
- `@OneToMany`: 1:N 관계 (Station ↔ ChargePoint, ChargePoint ↔ Connector)
- `@ManyToOne`: N:1 관계
- `@JoinColumn`: 외래 키 매핑
- `@JoinColumns`: 복합 외래 키 매핑
- `mappedBy`: 양방향 관계의 주인 설정
- `cascade`: 영속성 전이 (CascadeType.ALL)
- `orphanRemoval`: 고아 객체 제거
- `fetch`: 지연 로딩(LAZY) vs 즉시 로딩(EAGER)

### 3. 상속 관계 매핑
- `@MappedSuperclass`: BaseEntity를 통한 공통 필드 관리
- `@CreationTimestamp`, `@UpdateTimestamp`: 생성/수정 일시 자동 관리

### 4. 복합 유니크 키
- `@UniqueConstraint`: 테이블 레벨 복합 유니크 키 설정
- ChargePoint: (charge_point_id, station_id)
- Connector: (charge_point_id, station_id, connector_id)

### 5. Repository 패턴
- Spring Data JPA의 `JpaRepository` 상속
- 메서드 이름 규칙을 통한 쿼리 자동 생성
- `@Query`: JPQL을 사용한 커스텀 쿼리
- `LEFT JOIN FETCH`: N+1 문제 해결

## 환경 설정

### 필수 요구사항
- Java 25
- Gradle 8.12 이상 (또는 포함된 Gradle Wrapper 사용)
- Oracle Database (또는 H2 Database for 테스트)

### 데이터베이스 설정

#### Oracle 사용 시
`src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 수정하세요:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:xe
    username: your_username
    password: your_password
```

#### H2 Database 사용 시 (개발/테스트)
개발 프로파일로 실행하면 H2 인메모리 데이터베이스를 사용합니다:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

H2 콘솔 접속: http://localhost:8080/h2-console

## 실행 방법

### 0. Gradle Wrapper 초기화 (최초 1회만)
프로젝트를 처음 받았을 때, Gradle Wrapper를 초기화해야 합니다:

```bash
gradle wrapper --gradle-version=8.12
```

### 1. 프로젝트 빌드
```bash
./gradlew clean build
```

### 2. 애플리케이션 실행
```bash
# 기본 실행 (Oracle)
./gradlew bootRun

# 개발 환경 실행 (H2)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 3. Oracle 스키마 생성
Oracle Database를 사용하는 경우, `src/main/resources/schema.sql` 파일의 DDL을 실행하세요.

```bash
sqlplus username/password@database < src/main/resources/schema.sql
```

## JPA 학습 실습 예제

### 예제 1: 충전소 생성 및 조회
```java
@Service
@Transactional
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    // 충전소 생성
    public Station createStation() {
        Station station = Station.builder()
            .stationId("ST-001")
            .powerGridCapacity(new BigDecimal("500.00"))
            .maxPriceLimit(new BigDecimal("1000.00"))
            .algorithmMode(1)
            .timeExtensionFactor(new BigDecimal("1.5"))
            .maxIterationCount(10)
            .billingPowerId(1L)
            .build();

        return stationRepository.save(station);
    }

    // 충전소 조회
    public Optional<Station> findStation(String stationId) {
        return stationRepository.findByStationId(stationId);
    }
}
```

### 예제 2: 양방향 관계 설정
```java
// 충전소에 충전기 추가
Station station = stationRepository.findByStationId("ST-001")
    .orElseThrow();

ChargePoint chargePoint = ChargePoint.builder()
    .chargePointId("CP-001")
    .maxPower(new BigDecimal("100.00"))
    .build();

// 헬퍼 메서드를 사용한 양방향 관계 설정
station.addChargePoint(chargePoint);

stationRepository.save(station);
```

### 예제 3: N+1 문제 해결
```java
// N+1 문제 발생 가능
Station station = stationRepository.findByStationId("ST-001")
    .orElseThrow();
List<ChargePoint> chargePoints = station.getChargePoints(); // 추가 쿼리 발생

// JOIN FETCH로 해결
Station station = stationRepository.findByStationIdWithChargePoints("ST-001")
    .orElseThrow();
List<ChargePoint> chargePoints = station.getChargePoints(); // 추가 쿼리 없음
```

## 학습 주제별 참고 파일

| 학습 주제 | 참고 파일 |
|---------|---------|
| 기본 엔티티 매핑 | `Station.java` |
| 연관관계 매핑 | `ChargePoint.java`, `Connector.java` |
| 복합 유니크 키 | `ChargePoint.java`, `Connector.java` |
| 상속 관계 매핑 | `BaseEntity.java` |
| Repository 패턴 | `*Repository.java` |
| N+1 문제 해결 | `StationRepository.java`, `ChargePointRepository.java` |
| 양방향 관계 | `Station.java`, `ChargePoint.java` |

## 주의사항

### 1. 양방향 관계 관리
- 양방향 관계 설정 시 반드시 양쪽 모두 설정해야 합니다
- 편의 메서드(`addChargePoint`, `addConnector`)를 사용하세요

### 2. 지연 로딩
- 연관관계는 기본적으로 LAZY 로딩을 사용합니다
- 필요한 경우 JOIN FETCH를 사용하여 N+1 문제를 해결하세요

### 3. 복합 유니크 키
- ChargePoint와 Connector는 복합 유니크 키를 사용합니다
- 데이터 저장 시 중복 체크에 주의하세요

### 4. Cascade와 OrphanRemoval
- `cascade = CascadeType.ALL`: 부모 엔티티 저장/삭제 시 자식도 함께 처리
- `orphanRemoval = true`: 부모와 연관관계가 끊어진 자식 엔티티 자동 삭제

## 추가 학습 자료

- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate ORM 문서](https://hibernate.org/orm/documentation/)
- [JPA 스펙 문서](https://jakarta.ee/specifications/persistence/)

## 라이센스

학습 목적으로 자유롭게 사용 가능합니다.
