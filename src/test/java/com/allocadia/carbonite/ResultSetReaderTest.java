package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;

import java.sql.ResultSet;

import lombok.SneakyThrows;

public class ResultSetReaderTest {

    private static final String ID_LABEL = "ID";

    private static final String COLUMN_LABEL = "STRING_FIELD";
    
    private <T> ResultSetReader<T> newResultSetReader(PersistenceInfo<T> info) {
        PersistedObjectCache pom = new PersistedObjectCache();
        return new ResultSetReader<>(pom, info);
    }

    private ResultSetReader<TestClass> reader;
    
    @Mock
    private ResultSet resultSet;
    @Mock
    private ResultSet resultSet2;
    
    @Before
    @SneakyThrows
    public void before() {
        MockitoAnnotations.initMocks(this);
        reader = newResultSetReader(new PersistenceInfo<>(TestClass.class, ImmutableMap.of(
            ID_LABEL, TestClass.class.getDeclaredField("id"),
            COLUMN_LABEL, TestClass.class.getDeclaredField("stringField")
        ), ID_LABEL));
        
        Mockito.when(resultSet.next()).thenThrow(new Error("Should never call ResultSet.next when processing a single row!"));
    }

    @Test
    @SneakyThrows
    public void testMapRow() {
        String TEST_VALUE = "TEST";
        
        Mockito.when(resultSet.getObject(COLUMN_LABEL)).thenReturn(TEST_VALUE);
        Mockito.when(resultSet.getObject(ID_LABEL)).thenReturn(123);
        
        TestClass actual = reader.read(resultSet);
        
        assertEquals(TEST_VALUE, actual.getStringField());
    }

    @Test
    @SneakyThrows
    public void shouldUpdateExistingObjectWithNewColumnValue() {
        String TEST_VALUE_1 = "TEST";

        Mockito.when(resultSet.getObject(COLUMN_LABEL)).thenReturn(TEST_VALUE_1);
        Mockito.when(resultSet.getObject(ID_LABEL)).thenReturn(123);

        TestClass firstResult = reader.read(resultSet);
        assertEquals(TEST_VALUE_1, firstResult.getStringField());

        String TEST_VALUE_2 = "NEW_VALUE";
        
        Mockito.when(resultSet2.getObject(COLUMN_LABEL)).thenReturn(TEST_VALUE_2);
        Mockito.when(resultSet2.getObject(ID_LABEL)).thenReturn(123);

        TestClass secondResult = reader.read(resultSet2);
        
        assertTrue(firstResult == secondResult);
        assertEquals(TEST_VALUE_2, firstResult.getStringField());
    }

    @Test
    @SneakyThrows
    public void shouldUpdateExistingObjectWithNewColumnNullValue() {
        String TEST_VALUE_1 = "TEST";

        Mockito.when(resultSet.getObject(COLUMN_LABEL)).thenReturn(TEST_VALUE_1);
        Mockito.when(resultSet.getObject(ID_LABEL)).thenReturn(123);

        TestClass firstResult = reader.read(resultSet);
        assertEquals(TEST_VALUE_1, firstResult.getStringField());

        String TEST_VALUE_2 = null;
        
        Mockito.when(resultSet2.getObject(COLUMN_LABEL)).thenReturn(TEST_VALUE_2);
        Mockito.when(resultSet2.getObject(ID_LABEL)).thenReturn(123);

        TestClass secondResult = reader.read(resultSet2);
        
        assertTrue(firstResult == secondResult);
        assertEquals(TEST_VALUE_2, firstResult.getStringField());
    }
}
