package com.allocadia.carbonite;

import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Map;

import lombok.SneakyThrows;

public class DynamicRowMapper<T> implements RowMapper<T> {
    
    private final PersistenceInfo<T> info;
    
    public DynamicRowMapper(PersistenceInfo<T> info) {
        this.info = info;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SneakyThrows
    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        final T result = (T) info.getClazz().newInstance();

        for (Map.Entry<String, Field> entry : info.getColumn2field().entrySet()) {
            final String columnName = entry.getKey();
            final Field field = entry.getValue();

            Object value = rs.getObject(columnName);
            if (rs.wasNull()) {
                value = null;
            }

            if (null != value) {
                final Class<?> fieldClazz = field.getType();
                if (fieldClazz.isEnum()) {
                    value = Enum.valueOf((Class<Enum>) fieldClazz, (String)value);
                }
            }

            field.set(result, value);
        }

        return result;
    }
}
