package com.example.steamreplica.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CategoryUpdateEvent extends ApplicationEvent {
    private long id;

    public CategoryUpdateEvent(Object source, long id) {
        super(source);
        this.id = id;
    }
}
