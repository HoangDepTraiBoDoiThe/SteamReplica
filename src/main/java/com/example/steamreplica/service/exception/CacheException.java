package com.example.steamreplica.service.exception;

public class CacheException extends RuntimeException {
    public CacheException(String message) {
        super("Cache exception: " + message);
    }
    public CacheException(String message, Throwable cause) {
        super("Cache exception: " + message, cause);
    }
}