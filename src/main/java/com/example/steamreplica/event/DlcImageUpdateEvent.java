package com.example.steamreplica.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DlcImageUpdateEvent extends ApplicationEvent {
    private long id;

    public DlcImageUpdateEvent(Object source, long id) {
        super(source);
        this.id = id;
    }
}
