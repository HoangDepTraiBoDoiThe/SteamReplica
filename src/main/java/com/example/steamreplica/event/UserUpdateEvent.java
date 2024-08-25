package com.example.steamreplica.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserUpdateEvent extends ApplicationEvent {
    private long id;

    public UserUpdateEvent(Object source, long id) {
        super(source);
        this.id = id;
    }
}
