package com.example.steamreplica.service.exception;

public class GameException extends RuntimeException {
    public GameException(String message) {
        super("Game exception: " + message);
    }
}
