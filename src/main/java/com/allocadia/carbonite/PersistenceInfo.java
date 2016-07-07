package com.allocadia.carbonite;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.util.Map;

import lombok.Data;

@Data
public final class PersistenceInfo<T> {
    private final Class<T> clazz;
    private final String idField;
    private final Map<String, Field> column2field;

    public PersistenceInfo(Class<T> clazz, Map<String, Field> fields, String idField) {
        this(fields, idField, clazz);

        fields.values().forEach(field -> field.setAccessible(true));
    }

    //Private version that does not do any validation / modifying of fields
    private PersistenceInfo(Map<String, Field> fields, String idField, Class<T> clazz) {
        this.clazz = clazz;
        this.idField = idField;
        this.column2field = fields;
    }

    public PersistenceInfo<T> aliased(String alias) {
        if (null == alias) {
            return this;
        }

        ImmutableMap.Builder<String, Field> aliasedFields = ImmutableMap.builder();

        for (Map.Entry<String, Field> entry : column2field.entrySet()) {
            aliasedFields.put(alias + '.' + removeAlias(entry.getKey()), entry.getValue());
        }

        return new PersistenceInfo<>(aliasedFields.build(), alias + '.' + removeAlias(this.idField), clazz);
    }

    private String removeAlias(String key) {
        return key.replaceFirst(".+\\.", "");
    }
}
