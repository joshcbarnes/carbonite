package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DynamicRowMapperTest {

    private static final String COLUMN_LABEL = "FIELD1";

    private DynamicRowMapper<TestClass> rowMapper;
    
    @Mock
    private ResultSet resultSet;
    
    @Before
    @SneakyThrows
    public void before() {
        MockitoAnnotations.initMocks(this);
        Map<String, Field> fieldMap = new HashMap<>();
        Field field1 = TestClass.class.getDeclaredField("field1");
        field1.setAccessible(true);
        fieldMap.put(COLUMN_LABEL, field1);
        rowMapper = new DynamicRowMapper<>(TestClass.class, "alias", fieldMap);
    }
    
    @Test
    @SneakyThrows
    public void testMapRow() {
        String TEST_VALUE = "TEST";
        
        ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnLabel(1)).thenReturn(COLUMN_LABEL);
        Mockito.when(resultSet.getObject(1)).thenReturn(TEST_VALUE);
        
        TestClass actual = rowMapper.mapRow(resultSet, 0);
        
        assertEquals(TEST_VALUE, actual.getField1());
    }
}
