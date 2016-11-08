package com.allocadia.carbonite.write;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.allocadia.carbonite.TestClass;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JdbcTemplate.class)
public class CarbonitePersistTest {

    private CarbonitePersist<TestClass> carbonitePersist;
    
    @Mock
    private JdbcTemplate jdbcTemplate;
    
    @Before
    public void before() {
        PowerMockito.replace(PowerMockito.method(JdbcTemplate.class, "getJdbcTemplate")).with(new InvocationHandler() {
            
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return jdbcTemplate;
            }
        });
    }
    
    @Test
    public void test
}
