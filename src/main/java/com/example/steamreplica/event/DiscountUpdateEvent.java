package com.example.steamreplica.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DiscountUpdateEvent extends ApplicationEvent {
    private long id;

    public DiscountUpdateEvent(Object source, long id) {
        super(source);
        this.id = id;
    }
}
