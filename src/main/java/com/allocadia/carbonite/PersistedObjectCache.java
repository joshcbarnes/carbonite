package com.allocadia.carbonite;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class PersistedObjectCache {
    private final Map<Class<?>, Map<?, ?>> objectInstances = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <T, I> Map<I, T> getInstanceCache(Class<T> clazz,/*UNUSED: just for generics*/ I id) {
        return (Map<I, T>) objectInstances.computeIfAbsent(clazz, (c) -> new HashMap<>());
    }

    public <T, I> T getObjectById(Class<T> clazz, I id) {
        return getInstanceCache(clazz, id).get(Objects.requireNonNull(id));
    }

    public <T, I> T getObjectById(Class<T> clazz, I id, Function<I, T> fetcher) {
        return getInstanceCache(clazz, id).computeIfAbsent(Objects.requireNonNull(id), fetcher.andThen(Objects::requireNonNull));
    }
}
