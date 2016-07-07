package com.allocadia.carbonite;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.util.Map;

import lombok.Data;

@Data
public final class PersistenceInfo<T> {
    private final Class<T> clazz;
    private final Map<String, Field> column2field;

    public PersistenceInfo(Class<T> clazz, Map<String, Field> fields) {
        this(fields, clazz);

        fields.values().forEach(field -> field.setAccessible(true));
    }

    //Private version that does not do any validation / modifying of fields
    private PersistenceInfo(Map<String, Field> fields, Class<T> clazz) {
        this.clazz = clazz;
        this.column2field = fields;
    }

    public PersistenceInfo<T> aliased(String alias) {
        if (null == alias) {
            return this;
        }

        ImmutableMap.Builder<String, Field> aliasedFields = ImmutableMap.builder();

        for (Map.Entry<String, Field> entry : column2field.entrySet()) {
            aliasedFields.put(alias + '.' + entry.getKey().replaceFirst(".+\\.", ""), entry.getValue());
        }

        return new PersistenceInfo<>(aliasedFields.build(), clazz);
    }
}
