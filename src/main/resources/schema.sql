-- OCPP 2.0 충전기 도메인 스키마
-- Oracle Database 기반

-- 충전소 테이블 생성
CREATE TABLE STATION (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    station_id VARCHAR2(50) UNIQUE NOT NULL,
    power_grid_capacity NUMBER(10,2) NOT NULL,
    max_price_limit NUMBER(10,2) NOT NULL,
    algorithm_mode NUMBER(1) NOT NULL,
    time_extension_factor NUMBER(5,2) NOT NULL,
    max_iteration_count NUMBER(5) NOT NULL,
    billing_power_id NUMBER NOT NULL,
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE DEFAULT SYSDATE NOT NULL
);

-- 충전기 테이블 생성
CREATE TABLE CHARGE_POINT (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    charge_point_id VARCHAR2(50) NOT NULL,
    station_id VARCHAR2(50) NOT NULL,
    max_power NUMBER(10,2) NOT NULL,
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE DEFAULT SYSDATE NOT NULL
);

-- CHARGE_POINT 복합 유니크 인덱스 생성
CREATE UNIQUE INDEX idx_charge_point_unique
ON CHARGE_POINT(charge_point_id, station_id);

-- 커넥터 테이블 생성
CREATE TABLE CONNECTOR (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    charge_point_id VARCHAR2(50) NOT NULL,
    station_id VARCHAR2(50) NOT NULL,
    connector_id NUMBER(2) NOT NULL,
    max_power NUMBER(10,2) NOT NULL,
    min_power NUMBER(10,2) NOT NULL,
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE DEFAULT SYSDATE NOT NULL
);

-- CONNECTOR 복합 유니크 인덱스 생성
CREATE UNIQUE INDEX idx_connector_unique
ON CONNECTOR(charge_point_id, station_id, connector_id);

-- STATION 테이블 업데이트 트리거
CREATE OR REPLACE TRIGGER trg_station_update
BEFORE UPDATE ON STATION
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSDATE;
END;
/

-- CHARGE_POINT 테이블 업데이트 트리거
CREATE OR REPLACE TRIGGER trg_charge_point_update
BEFORE UPDATE ON CHARGE_POINT
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSDATE;
END;
/

-- CONNECTOR 테이블 업데이트 트리거
CREATE OR REPLACE TRIGGER trg_connector_update
BEFORE UPDATE ON CONNECTOR
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSDATE;
END;
/

-- 테이블 주석
COMMENT ON TABLE STATION IS '충전소 정보 테이블';
COMMENT ON TABLE CHARGE_POINT IS '충전기 정보 테이블';
COMMENT ON TABLE CONNECTOR IS '커넥터 정보 테이블';

-- STATION 컬럼 주석
COMMENT ON COLUMN STATION.id IS 'ID';
COMMENT ON COLUMN STATION.station_id IS '충전소 ID';
COMMENT ON COLUMN STATION.power_grid_capacity IS '전력 최대 수용량(kW)';
COMMENT ON COLUMN STATION.max_price_limit IS '최대 허용 가격(원)';
COMMENT ON COLUMN STATION.algorithm_mode IS '스마트충전 알고리즘 모드';
COMMENT ON COLUMN STATION.time_extension_factor IS '시간확장 계수';
COMMENT ON COLUMN STATION.max_iteration_count IS '최대 반복 횟수';
COMMENT ON COLUMN STATION.billing_power_id IS '요금 적용 전력 ID';
COMMENT ON COLUMN STATION.created_at IS '생성일시';
COMMENT ON COLUMN STATION.updated_at IS '수정일시';

-- CHARGE_POINT 컬럼 주석
COMMENT ON COLUMN CHARGE_POINT.id IS 'ID';
COMMENT ON COLUMN CHARGE_POINT.charge_point_id IS '충전기 ID';
COMMENT ON COLUMN CHARGE_POINT.station_id IS '충전소 ID';
COMMENT ON COLUMN CHARGE_POINT.max_power IS '최대 허용 전력량(kW)';
COMMENT ON COLUMN CHARGE_POINT.created_at IS '생성일시';
COMMENT ON COLUMN CHARGE_POINT.updated_at IS '수정일시';

-- CONNECTOR 컬럼 주석
COMMENT ON COLUMN CONNECTOR.id IS 'ID';
COMMENT ON COLUMN CONNECTOR.station_id IS '충전소 ID';
COMMENT ON COLUMN CONNECTOR.charge_point_id IS '충전기 ID';
COMMENT ON COLUMN CONNECTOR.connector_id IS '커넥터 ID';
COMMENT ON COLUMN CONNECTOR.max_power IS '최대 허용 전력량(kW)';
COMMENT ON COLUMN CONNECTOR.min_power IS '최소 허용 전력량(kW)';
COMMENT ON COLUMN CONNECTOR.created_at IS '생성일시';
COMMENT ON COLUMN CONNECTOR.updated_at IS '수정일시';
