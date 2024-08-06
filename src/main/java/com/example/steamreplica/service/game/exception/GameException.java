package com.example.steamreplica.service.game.exception;

public class GameException extends RuntimeException {
    public GameException(String message) {
        super("Game exception: " + message);
    }
}
