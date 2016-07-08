package com.allocadia.carbonite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class ModifiedObjects<T extends PersistedObject> {

    private List<T> dirtyObjects;
    private List<T> newObjects;
    
    public static <T extends PersistedObject> ModifiedObjects<T> from(Collection<T> objects) {
        ModifiedObjects<T> modifiedObjects = new ModifiedObjects<>();
        
        for (T object : objects) {
            if (object.getState().equals(PersistedObject.State.NEW)) {
                modifiedObjects.getNewObjects().add(object);
            }
            if (object.getState().equals(PersistedObject.State.DIRTY)) {
                modifiedObjects.getNewObjects().add(object);
            }
        }
        
        return modifiedObjects;
    }

    public Collection<T> getCombined() {
        List<T> combined = new ArrayList<>(dirtyObjects.size() + newObjects.size());
        combined.addAll(dirtyObjects);
        combined.addAll(newObjects);
        return combined;
    }
}
