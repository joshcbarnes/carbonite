package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.allocadia.carbonite.annotation.Persist;
import com.allocadia.carbonite.utils.QueryUtils;

public class CarboniteQueryTest {

    private CarboniteQuery<TestClass> query;
    
    @Mock
    private JdbcTemplate mockTemplate;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        query = Mockito.spy(new CarboniteQuery<TestClass>(TestClass.class));
        Mockito.doReturn(mockTemplate).when((JdbcDaoSupport) query).getJdbcTemplate();
    }
    
    //TODO: Figure out how to do mocking properly
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    @Test
//    public void testRun() {
//        String sql = "select * as alias from something where id = ?";
//        String alias = "alias";
//        Object[] param = new Object[]{5};
//        
//        query
//            .withSql(sql)
//            .withParams(param)
//            .withAlias(alias)
//            .run();
//        
//        ArgumentCaptor<DynamicRowMapper> captor = ArgumentCaptor.forClass(DynamicRowMapper.class);
//        Mockito.verify(mockTemplate).query(sql, new Object[]{param}, captor.capture());
//        
//        DynamicRowMapper<TestClass> rowMapper = captor.getValue();
//        assertEquals(alias, rowMapper.getAlias());
//        assertEquals(TestClass.class, rowMapper.getResultClass());
//        assertFieldMapCorrectness(TestClass.class, rowMapper.getFieldMap());
//    }
    
    private static void assertFieldMapCorrectness(Class<?> clazz, Map<String, Field> fieldMap) {
        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(Persist.class)) {
                if ("".equals(field.getAnnotation(Persist.class).column())) {
                    assertTrue(fieldMap.containsKey(QueryUtils.camel2underscore(field.getName())));
                    assertEquals(field, fieldMap.get(QueryUtils.camel2underscore(field.getName())));
                } else {
                    assertTrue(fieldMap.containsKey(field.getAnnotation(Persist.class).column()));
                    assertEquals(field, fieldMap.get(field.getAnnotation(Persist.class).column()));
                }
            }
        }
    }
}
