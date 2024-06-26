package com.example.sandbox.exception;

public class TokenInvalidExpiredException extends RuntimeException {
    public TokenInvalidExpiredException() {
    }

    public TokenInvalidExpiredException(String message) {
        super(message);
    }
}
