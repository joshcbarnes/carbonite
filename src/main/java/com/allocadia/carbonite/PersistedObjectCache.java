package com.allocadia.carbonite;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class PersistedObjectCache {
    private final Map<Class<?>, Map<?, ?>> objectInstances = new HashMap<>();

    public <T, I> T getObjectById(Class<T> clazz, I id) {
        @SuppressWarnings("unchecked")
        final Map<I, T> objectCache = (Map<I, T>) objectInstances.computeIfAbsent(clazz, (c) -> new HashMap<>());
        return objectCache.get(Objects.requireNonNull(id));
    }

    public <T, I> T getObjectById(Class<T> clazz, I id, Function<I, T> fetcher) {
        @SuppressWarnings("unchecked")
        final Map<I, T> objectCache = (Map<I, T>) objectInstances.computeIfAbsent(clazz, (c) -> new HashMap<>());
        return objectCache.computeIfAbsent(Objects.requireNonNull(id), fetcher.andThen(Objects::requireNonNull));
    }
}
