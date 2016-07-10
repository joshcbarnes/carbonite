package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.SneakyThrows;

public class ResultSetReaderTest {

    private static final int ID_DEFAULT = 123;
    private static final String STRING_DEFAULT = "TEST";
    private static final int INT_DEFAULT = 456;
    private static final Integer INTEGER_DEFAULT = 789;
    private static final String CUSTOM_DEFAULT = "ABC";
    private static final String PUBLIC_DEFAULT = "DEF";

    private static final String ID_LABEL = "ID";
    private static final String STRING_LABEL = "STRING_FIELD";
    private static final String INT_LABEL = "INT_FIELD";
    private static final String INTEGER_LABEL = "INTEGER_FIELD";
    private static final String CUSTOM_LABEL = "CUSTOM_COLUMN_NAME";
    private static final String PUBLIC_LABEL = "PUBLIC_FIELD";

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
        
        reader = newResultSetReader(PersistenceInfo.from(TestClass.class));
        Mockito.when(resultSet.next()).thenThrow(new Error("Should never call ResultSet.next when processing a single row!"));

        initResultSet(resultSet);
        initResultSet(resultSet2);
    }

    private void initResultSet(ResultSet resultSet) throws SQLException {
        Mockito.when(resultSet.getObject(STRING_LABEL)).thenReturn(STRING_DEFAULT);
        Mockito.when(resultSet.getObject(ID_LABEL)).thenReturn(ID_DEFAULT);
        Mockito.when(resultSet.getObject(INT_LABEL)).thenReturn(INT_DEFAULT);
        Mockito.when(resultSet.getObject(INTEGER_LABEL)).thenReturn(INTEGER_DEFAULT);
        Mockito.when(resultSet.getObject(CUSTOM_LABEL)).thenReturn(CUSTOM_DEFAULT);
        Mockito.when(resultSet.getObject(PUBLIC_LABEL)).thenReturn(PUBLIC_DEFAULT);
    }

    @Test
    @SneakyThrows
    public void testMapRow() {
        TestClass actual = reader.read(resultSet);
        
        assertEquals(STRING_DEFAULT, actual.getStringField());
    }

    @Test
    @SneakyThrows
    public void shouldUpdateExistingObjectWithNewColumnValue() {
        TestClass firstResult = reader.read(resultSet);
        assertEquals(STRING_DEFAULT, firstResult.getStringField());

        String TEST_VALUE_2 = "NEW_VALUE";
        
        Mockito.when(resultSet2.getObject(STRING_LABEL)).thenReturn(TEST_VALUE_2);

        TestClass secondResult = reader.read(resultSet2);
        
        assertTrue(firstResult == secondResult);
        assertEquals(TEST_VALUE_2, firstResult.getStringField());
    }

    @Test
    @SneakyThrows
    public void shouldUpdateExistingObjectWithNewColumnNullValue() {
        TestClass firstResult = reader.read(resultSet);
        assertEquals(STRING_DEFAULT, firstResult.getStringField());

        String TEST_VALUE_2 = null;
        
        Mockito.when(resultSet2.getObject(STRING_LABEL)).thenReturn(TEST_VALUE_2);

        TestClass secondResult = reader.read(resultSet2);
        
        assertTrue(firstResult == secondResult);
        assertEquals(TEST_VALUE_2, firstResult.getStringField());
    }
}
