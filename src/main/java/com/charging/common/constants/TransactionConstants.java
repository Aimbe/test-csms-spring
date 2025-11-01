package com.charging.common.constants;

/**
 * 트랜잭션 관련 상수
 */
public final class TransactionConstants {

    private TransactionConstants() {
        throw new AssertionError("상수 클래스는 인스턴스화할 수 없습니다");
    }

    /**
     * 트랜잭션 ID 접두사
     */
    public static final String TRANSACTION_ID_PREFIX = "TXN-";

    /**
     * 기본 종료 이유
     */
    public static final String DEFAULT_STOP_REASON = "Normal";

    /**
     * 종료 이유 - 사용자 요청
     */
    public static final String STOP_REASON_USER = "User";

    /**
     * 종료 이유 - 에너지 제한
     */
    public static final String STOP_REASON_ENERGY_LIMIT = "EnergyLimit";

    /**
     * 종료 이유 - 시간 제한
     */
    public static final String STOP_REASON_TIME_LIMIT = "TimeLimit";

    /**
     * 종료 이유 - 긴급 정지
     */
    public static final String STOP_REASON_EMERGENCY_STOP = "EmergencyStop";

    /**
     * 종료 이유 - 오류
     */
    public static final String STOP_REASON_ERROR = "Error";
}
