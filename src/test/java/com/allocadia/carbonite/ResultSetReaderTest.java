package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;

import java.sql.ResultSet;

import lombok.SneakyThrows;

public class ResultSetReaderTest {

    private static final String COLUMN_LABEL = "FIELD1";

    private ResultSetReader<TestClass> reader;
    
    @Mock
    private ResultSet resultSet;
    
    @Before
    @SneakyThrows
    public void before() {
        MockitoAnnotations.initMocks(this);
        reader = new ResultSetReader<>(new PersistenceInfo<>(TestClass.class, ImmutableMap.of(
            COLUMN_LABEL, TestClass.class.getDeclaredField("field1")
        )));
        
        Mockito.when(resultSet.next()).thenThrow(new Error("Should never call ResultSet.next when processing a single row!"));
    }
    
    @Test
    @SneakyThrows
    public void testMapRow() {
        String TEST_VALUE = "TEST";
        
        Mockito.when(resultSet.getObject(COLUMN_LABEL)).thenReturn(TEST_VALUE);
        
        TestClass actual = reader.read(resultSet);
        
        assertEquals(TEST_VALUE, actual.getField1());
    }
}
