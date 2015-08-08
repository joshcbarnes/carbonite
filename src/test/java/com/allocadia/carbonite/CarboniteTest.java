package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CarboniteTest {

    private Carbonite carbonite;
    
    @Mock
    private DataSource dataSource;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        carbonite = new Carbonite();
        carbonite.setDataSource(dataSource);
    }
    
    @Test
    public void testNewQuery_receivesDataSource() {
        CarboniteQuery<String> actual = carbonite.newQuery(String.class);
        
        assertEquals(dataSource, actual.getDataSource());
    }
}
