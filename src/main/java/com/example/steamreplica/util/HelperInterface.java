package com.example.steamreplica.util;

public interface HelperInterface<T> {
    boolean isRelated(T entity, long id);
}
