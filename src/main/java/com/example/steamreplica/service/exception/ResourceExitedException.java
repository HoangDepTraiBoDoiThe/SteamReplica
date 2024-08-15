package com.example.steamreplica.service.exception;

public class ResourceExitedException extends RuntimeException {
    public ResourceExitedException(String message) {
        super("Resource exited exception: " + message);
    }
}