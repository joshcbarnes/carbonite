package com.allocadia.carbonite;

import com.allocadia.carbonite.exception.CarboniteUsageException;
import com.allocadia.carbonite.write.CarbonitePersist;

import java.util.Collection;

import javax.sql.DataSource;

import lombok.Data;
import lombok.NonNull;

@Data
public class CarboniteObjectManager {

    @NonNull
    private DataSource dataSource;
    
    private Carbonite carbonite;
    
    private PersistedObjectCache objectManager;
    
    public <T extends PersistedObject> CarboniteQuery<T> newQuery(Class<T> resultClass) {
        CarboniteQuery<T> carboniteQuery = new CarboniteQuery<>(getPersistedObjectManager(), carbonite.getPersistenceInfo(resultClass));
        carboniteQuery.setDataSource(dataSource);
        return carboniteQuery;
    }
    
    public PersistedObjectCache getPersistedObjectManager() {
        if (null == objectManager) {
            objectManager = new PersistedObjectCache();
        }
        return objectManager;
    }
    
    public <T extends PersistedObject> Collection<T> save(Collection<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return objects;
        }
        
        validateAllObjectsSameType(objects);
        
        CarbonitePersist<T> carbonitePersist = new CarbonitePersist<>(getPersistedObjectManager(), carbonite);
        carbonitePersist.setDataSource(dataSource);
        return carbonitePersist.save(objects);
    }
    
    private <T> void validateAllObjectsSameType(Collection<T> objects) {
        Class<?> clazz = objects.iterator().next().getClass();
        boolean allSame = objects.stream().allMatch(o -> o.getClass() == clazz);
        if (!allSame) {
            throw new CarboniteUsageException("All objects being persisted must be of the same type");
        }
    }
}
