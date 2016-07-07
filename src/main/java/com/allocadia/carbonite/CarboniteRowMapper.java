package com.allocadia.carbonite;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

import lombok.SneakyThrows;

public class CarboniteRowMapper<T> implements RowMapper<T> {
    
    private final ResultSetReader<T> reader;
    
    public CarboniteRowMapper(PersistenceInfo<T> info) {
        this.reader = new ResultSetReader<>(info);
    }

    @SneakyThrows
    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        return reader.read(rs);
    }
}
