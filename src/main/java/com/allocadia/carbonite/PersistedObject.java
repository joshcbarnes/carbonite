package com.allocadia.carbonite;

import java.util.Set;

public interface PersistedObject {
    Set<String> getDirty();
}