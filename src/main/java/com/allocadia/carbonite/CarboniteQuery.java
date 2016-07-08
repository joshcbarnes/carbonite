package com.allocadia.carbonite;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.google.common.collect.Lists;

import java.util.List;


public class CarboniteQuery<T> extends JdbcDaoSupport {

    private final PersistedObjectCache pom;
    private final PersistenceInfo<T> info;
    private PersistenceInfo<T> aliasedInfo;

    private String sql;
    private List<Object> params;
    
    public CarboniteQuery(PersistedObjectCache pom, PersistenceInfo<T> persistenceInfo) {
        this.pom = pom;
        this.info = persistenceInfo;
        this.aliasedInfo = this.info;
    }
    
    public CarboniteQuery<T> withSql(String sql) {
        this.sql = sql;
        return this;
    }
    
    public CarboniteQuery<T> withAlias(String alias) {
        this.aliasedInfo = info.aliased(alias);
        return this;
    }
    
    public CarboniteQuery<T> withParams(List<Object> params) {
        this.params = params;
        return this;
    }
    
    public CarboniteQuery<T> withParams(Object[] params) {
        this.params = Lists.newArrayList(params);
        return this;
    }
    
    public List<T> run() {
        return super.getJdbcTemplate().query(sql, params.toArray(), new CarboniteRowMapper<T>(pom, aliasedInfo));
    }
}
