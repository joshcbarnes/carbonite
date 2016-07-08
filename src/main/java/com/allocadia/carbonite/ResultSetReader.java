package com.allocadia.carbonite;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Map;

import lombok.SneakyThrows;

public class ResultSetReader<T> {
    private final PersistedObjectCache pom;
    private final Class<T> tClazz;
    private final Map<String, Field> column2field;
    private final String idFieldName;
    
    public ResultSetReader(PersistedObjectCache pom, PersistenceInfo<T> info) {
        this.pom = pom;
        this.tClazz = info.getClazz();
        this.column2field = info.getColumn2field();
        this.idFieldName = info.getIdField();
    }

    @SneakyThrows
    private T createInstance(Object id) {
        return tClazz.newInstance();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SneakyThrows
    public T read(ResultSet rs) {
        final T result = pom.getObjectById(tClazz, rs.getObject(idFieldName), this::createInstance);

        for (Map.Entry<String, Field> entry : column2field.entrySet()) {
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
