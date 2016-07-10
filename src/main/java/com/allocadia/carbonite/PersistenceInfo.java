package com.allocadia.carbonite;

import com.allocadia.carbonite.annotation.Id;
import com.allocadia.carbonite.annotation.Persist;
import com.allocadia.carbonite.utils.QueryUtils;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterators;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import lombok.Data;

@Data
public final class PersistenceInfo<T> {
    private final Class<T> clazz;
    private final String tableName;
    private final String idField;
    private final Map<String, Field> column2field;
    private final Map<Field, String> field2Column;

    public static <T> PersistenceInfo<T> from(Class<T> clazz) {
        return new PersistenceInfo<>(clazz, getTableName(clazz), getFields(clazz), getIdField(clazz));
    }
    
    private PersistenceInfo(Class<T> clazz, String tableName, ImmutableBiMap<String, Field> fields, String idField) {
        this(fields, Objects.requireNonNull(idField), Objects.requireNonNull(clazz), tableName);

        fields.values().forEach(field -> field.setAccessible(true));
    }

    //Private version that does not do any validation / modifying of fields
    private PersistenceInfo(ImmutableBiMap<String, Field> fields, String idField, Class<T> clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.idField = idField;
        this.column2field = fields;
        this.field2Column = fields.inverse();
    }

    public PersistenceInfo<T> aliased(String alias) {
        if (null == alias) {
            return this;
        }

        ImmutableBiMap.Builder<String, Field> aliasedFields = ImmutableBiMap.builder();

        for (Map.Entry<String, Field> entry : column2field.entrySet()) {
            aliasedFields.put(alias + '.' + removeAlias(entry.getKey()), entry.getValue());
        }

        return new PersistenceInfo<>(aliasedFields.build(), alias + '.' + removeAlias(this.idField), clazz, alias);
    }

    private String removeAlias(String key) {
        return key.replaceFirst(".+\\.", "");
    }
    
    private static String getTableName(Class<?> clazz) {
        //TODO: Accomodate table names specified through annotation?
        
        return clazz.getSimpleName().toUpperCase();
    }
    
    private static String getIdField(Class<?> clazz) {
        return Iterators.getOnlyElement(
            Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(field -> {
                    Persist p = field.getAnnotation(Persist.class);
                    if (null != p && !p.column().isEmpty()) {
                        return p.column();
                    }
    
                    return QueryUtils.camel2underscore(field.getName());
                })
                .iterator()
        );
    }

    private static ImmutableBiMap<String, Field> getFields(Class<?> clazz) {
        ImmutableBiMap.Builder<String, Field> builder = ImmutableBiMap.builder();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Persist.class) || field.isAnnotationPresent(Id.class)) {
                final String name;

                final Persist p = field.getAnnotation(Persist.class);
                if (null != p && !p.column().isEmpty()) {
                    name = p.column();
                } else {
                    name = QueryUtils.camel2underscore(field.getName());
                }

                builder.put(name, field);
            }
        }

        return builder.build();
    }
}
