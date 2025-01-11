package com.example.pdca.exception;

/**
 * 用户已存在异常
 * 当尝试注册已存在的用户时抛出
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
} 