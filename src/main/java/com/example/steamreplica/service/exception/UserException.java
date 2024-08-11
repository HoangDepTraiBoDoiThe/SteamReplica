package com.example.steamreplica.service.exception;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super("User exception: " + message);
    }
}