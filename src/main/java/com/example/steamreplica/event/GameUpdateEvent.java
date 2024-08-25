package com.example.steamreplica.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameUpdateEvent extends ApplicationEvent {
    private long id;

    public GameUpdateEvent(Object source, long id) {
        super(source);
        this.id = id;
    }
}
