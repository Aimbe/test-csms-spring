package com.charging.exception;

/**
 * 충전 도메인 기본 예외
 */
public class ChargingException extends RuntimeException {

    public ChargingException(String message) {
        super(message);
    }

    public ChargingException(String message, Throwable cause) {
        super(message, cause);
    }
}
