package com.allocadia.carbonite.write;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.allocadia.carbonite.Carbonite;
import com.allocadia.carbonite.CarboniteRowMapper;
import com.allocadia.carbonite.ModifiedObjects;
import com.allocadia.carbonite.PersistedObject;
import com.allocadia.carbonite.PersistedObjectCache;
import com.allocadia.carbonite.PersistenceInfo;
import com.allocadia.carbonite.utils.QueryUtils;
import com.google.common.base.Joiner;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

public class CarbonitePersist<T extends PersistedObject> extends JdbcDaoSupport {

    private final PersistedObjectCache poc;
    private final Carbonite carbonite;
    
    public CarbonitePersist(PersistedObjectCache poc, Carbonite carbonite) {
        this.poc = poc;
        this.carbonite = carbonite;
    }
    
    public Collection<T> save(Collection<T> objects) {
        ModifiedObjects<T> modifiedObjects = ModifiedObjects.from(objects);
        Class<?> clazz = objects.iterator().next().getClass();
        
        @SuppressWarnings("unchecked")
        PersistenceInfo<T> info = (PersistenceInfo<T>) carbonite.getPersistenceInfo(clazz);
        
        persistNew(modifiedObjects.getNewObjects(), info);
        persistDirty(modifiedObjects.getDirtyObjects(), info);
        return modifiedObjects.getCombined();
    }

    @SneakyThrows
    void persistDirty(List<T> dirtyObjects, PersistenceInfo<T> info) {
        for (int i = 0; i < dirtyObjects.size(); i++) {
            PersistedObject dirtyObject = dirtyObjects.get(i);
            List<String> dirtyFields = new ArrayList<>(dirtyObject.getDirty());
            
            String updateSql = Joiner.on("\n").join(
                "UPDATE " + info.getTableName(),
                "SET " + getFieldUpdateStmt(dirtyFields, info),
                "WHERE " + info.getField2Column().get(info.getIdField()) + " = ?");
            
            PreparedStatement ps = super.getJdbcTemplate().getDataSource().getConnection().prepareStatement(updateSql);
            for (int j = 0; j < dirtyFields.size(); j++) {
                Field field = info.getFieldName2Field().get(dirtyFields.get(j));
                ps.setObject(j, field.get(dirtyObject));
            }
            ps.executeUpdate();
            
            Number id = (Number) info.getColumn2field().get(info.getIdField()).get(dirtyObject);
            dirtyObjects.set(i, getUpdatedObject(info, id));
        }
    }

    private String getFieldUpdateStmt(List<String> dirtyFields, PersistenceInfo<T> info) {
        Map<String, Field> fieldName2Field = info.getFieldName2Field();
        Map<Field, String> field2Column = info.getField2Column();
        
        List<String> columnStmts = dirtyFields.stream()
            .map(f -> {
                Field field = fieldName2Field.get(f);
                return field2Column.get(field);
            })
            .map(cn -> cn + " = ?")
            .collect(Collectors.toList());
        
        return String.join(", ", columnStmts);
    }

    @SneakyThrows
    void persistNew(List<T> newObjects, PersistenceInfo<T> info) {
        for (int i = 0; i < newObjects.size(); i++) {
            PersistedObject newObject = newObjects.get(i);
            List<String> dirtyFields = new ArrayList<>(newObject.getDirty());
            String insertSql = Joiner.on("\n").join(
                "INSERT INTO " + info.getTableName(),
                "(" + String.join(", ", newObject.getDirty()) + ")",
                "VALUES",
                "(" + QueryUtils.createParamList(newObject.getDirty().size()) + ")");
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            super.getJdbcTemplate().update(
                new PreparedStatementCreator() {
                    
                    @SneakyThrows
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(insertSql);
                        for (int i = 0; i < dirtyFields.size(); i++) {
                            Field field = info.getFieldName2Field().get(dirtyFields.get(i));
                            ps.setObject(i, field.get(newObject));
                        }
                        return ps;
                    }
                },
                keyHolder);
            
            Number id = keyHolder.getKey();
            T persistedObject = getUpdatedObject(info, id);
            
            newObjects.set(i, persistedObject);
        }
    }

    private T getUpdatedObject(PersistenceInfo<T> info, Number id) {
        T persistedObject = super.getJdbcTemplate().query(
            "SELECT * FROM " + info.getTableName() + " WHERE " + info.getField2Column().get(info.getIdField()) + " = ?",
            new Object[]{id},
            new CarboniteRowMapper<>(poc, info))
                .get(0);
        return persistedObject;
    }
}
