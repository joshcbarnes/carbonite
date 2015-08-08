package com.allocadia.carbonite;

import javax.sql.DataSource;

import lombok.Data;

@Data
public class Carbonite {

    private DataSource dataSource;
    
    public <T> CarboniteQuery<T> newQuery(Class<T> resultClass) {
        CarboniteQuery<T> carboniteQuery = new CarboniteQuery<T>(resultClass);
        carboniteQuery.setDataSource(dataSource);
        return carboniteQuery;
    }
}
