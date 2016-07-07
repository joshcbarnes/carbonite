package com.allocadia.carbonite;

import javax.sql.DataSource;

import lombok.Data;
import lombok.NonNull;

@Data
public class CarboniteObjectManager {

    @NonNull
    private DataSource dataSource;
    
    private Carbonite carbonite;
    
    public <T> CarboniteQuery<T> newQuery(Class<T> resultClass) {
        CarboniteQuery<T> carboniteQuery = new CarboniteQuery<>(carbonite.getPersistenceInfo(resultClass));
        carboniteQuery.setDataSource(dataSource);
        return carboniteQuery;
    }
}
