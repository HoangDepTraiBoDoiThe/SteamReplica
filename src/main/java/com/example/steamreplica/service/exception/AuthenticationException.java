package com.example.steamreplica.service.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super("Authentication exception: " + message);
    }
}