package com.allocadia.carbonite;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import lombok.Data;
import lombok.SneakyThrows;

import org.springframework.jdbc.core.RowMapper;

@Data
public class DynamicRowMapper<T> implements RowMapper<T> {
    
    private Class<T> resultClass;
    private Map<String, Field> fieldMap;
    private String alias;
    
    public DynamicRowMapper(Class<T> resultClass, String alias, Map<String, Field> fieldMap) {
        this.resultClass = resultClass;
        this.fieldMap = fieldMap;
        this.alias = alias;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SneakyThrows
    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        T result = (T) resultClass.newInstance();
        
        ResultSetMetaData metadata = rs.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String columnName = metadata.getColumnLabel(i);
            Object value = rs.getObject(i);
            
            Field field;
            if (alias != null) {
                field = fieldMap.get(columnName.replaceFirst(alias + ".", ""));
            } else {
                field = fieldMap.get(columnName);
            }
            
            if (field == null) {
                continue;
            }
            
            Class<?> type = field.getType();
            
            if (Integer.class.isAssignableFrom(type)) {
                field.set(result, (Integer) value);
            } else if (String.class.isAssignableFrom(type)) {
                field.set(result, (String) value);
            } else if (Double.class.isAssignableFrom(type)) {
                field.set(result, (Double) value);
            } else if (Boolean.class.isAssignableFrom(type)) {
                field.set(result, (Boolean) value);
            } else if (Date.class.isAssignableFrom(type)) {
                field.set(result, (Date) value);
            } else if (Enum.class.isAssignableFrom(type)) {
                field.set(result, Enum.valueOf((Class<Enum>) type, (String) value));
            }
        }
        
        return result;
    }
}
