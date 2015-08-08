package com.allocadia.carbonite;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.allocadia.carbonite.annotation.Persist;
import com.allocadia.carbonite.utils.QueryUtils;
import com.google.common.collect.Lists;


public class CarboniteQuery<T> extends JdbcDaoSupport {

    private String sql;
    private Class<T> resultClass;
    private String alias;
    private List<Object> params;
    
    public CarboniteQuery(Class<T> resultClass) {
        this.resultClass = resultClass;
    }
    
    public CarboniteQuery<T> withSql(String sql) {
        this.sql = sql;
        return this;
    }
    
    public CarboniteQuery<T> withAlias(String alias) {
        this.alias = alias;
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
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : resultClass.getDeclaredFields()) {
            field.setAccessible(true);
              
            Persist persist = field.getAnnotation(Persist.class);
            if (persist != null) {
                String columnName = persist.column().isEmpty() ?
                    QueryUtils.camel2underscore(field.getName())
                    :
                    persist.column();
                fieldMap.put(columnName, field);
            }
        }
        
        return super.getJdbcTemplate().query(sql, params.toArray(), new DynamicRowMapper<T>(resultClass, alias, fieldMap));
    }
}
