package com.allocadia.carbonite;

import javax.sql.DataSource;

import lombok.Data;
import lombok.NonNull;

@Data
public class CarboniteObjectManager {

    @NonNull
    private DataSource dataSource;
    
    private Carbonite carbonite;
    
    private PersistedObjectCache objectManager;
    
    public <T> CarboniteQuery<T> newQuery(Class<T> resultClass) {
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
}
