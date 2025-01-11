package com.example.pdca.exception;

import org.springframework.http.HttpStatus;

/**
 * 自定义业务异常
 * 用于处理特定的业务逻辑错误
 */
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
} 