package com.allocadia.carbonite;

import java.util.Set;

public interface PersistedObject {
    enum State {
        NEW, DIRTY, CLEAN;
    }

    Set<String> getDirty();

    void markDirty(String fieldName);
    void markClean();

    State getState();
}