package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.allocadia.carbonite.annotation.Carbonated;
import com.allocadia.carbonite.annotation.Id;
import com.allocadia.carbonite.annotation.Persist;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class PersistenceInfoTest {
    private static final class NonCarbonatedClassWithId {
        @Id
        private Integer foo;
    }
    @Test
    public void shouldWorkOnAnyClass_asLongAsItHasAnId() {
        PersistenceInfo.from(NonCarbonatedClassWithId.class);
        PersistenceInfo.from(TestClass.class);
    }

    @Test
    public void shouldReadPersistFieldsAndIgnoreOthers() {
        Map<String, Field> persistentFields = PersistenceInfo.from(TestClass.class).getColumn2field();

        assertFalse(persistentFields.isEmpty());
        assertTrue(TestClass.class.getDeclaredFields().length > persistentFields.size());
    }

    @Test
    public void shouldConvertColumnNamesToUPPER_CASE_byDefault() {
        Set<String> columnNames = PersistenceInfo.from(TestClass.class).getColumn2field().keySet();
        assertTrue(columnNames.containsAll(Arrays.asList("STRING_FIELD", "INT_FIELD")));
    }

    @Test
    public void shouldReadPersistAnnotationColumnNameWhenPresent() {
        Set<String> columnNames = PersistenceInfo.from(TestClass.class).getColumn2field().keySet();

        assertFalse(columnNames.contains("CUSTOM_COLUMN_NAME"));
        assertTrue(columnNames.containsAll(Arrays.asList("field_NAME")));
    }
    
    @Test
    public void shouldReadIdAnnotation() {
        PersistenceInfo<TestClass> pi = PersistenceInfo.from(TestClass.class);

        assertEquals("ID", pi.getIdField());
        assertTrue(pi.getColumn2field().containsKey(pi.getIdField()));
    }

    @Carbonated
    private static final class IdAnnotationTest {
        @Id
        @Persist(column = "mi_XedC_asE")
        private Integer foo;
    }
    @Test
    public void shouldReadIdAnnotationColumnNameWhenPresent() {
        PersistenceInfo<IdAnnotationTest> pi = PersistenceInfo.from(IdAnnotationTest.class);
        
        assertEquals("mi_XedC_asE", pi.getIdField());
        assertTrue(pi.getColumn2field().containsKey(pi.getIdField()));
    }

    @Carbonated
    private static final class MultipleIdAnnotationTest {
        @Id
        private Integer a;
        @Id
        private Integer b;
    }
    @Test(expected = Exception.class)
    public void shouldThrowWhenMultipleIdAnnotations() {
        PersistenceInfo.from(MultipleIdAnnotationTest.class);
    }

    @Test(expected = Exception.class)
    public void shouldThrowWhithNoId() {
        PersistenceInfo.from(String.class);
    }
    
    @Carbonated
    private static final class CamalizedIdTest {
        @Id
        private Integer idWithCamels;
    }
    @Test
    public void shouldConvertCamelCaseIdsTo_UPPER_UNDERSCORE() {
        assertEquals("ID_WITH_CAMELS", PersistenceInfo.from(CamalizedIdTest.class).getIdField());
    }
    
    @Test
    public void shouldAliasOriginalName() throws Exception {
        PersistenceInfo<TestClass> pi = PersistenceInfo.from(TestClass.class);
        
        PersistenceInfo<TestClass> fooAlias = pi.aliased("foo");

        assertEquals(pi.getClazz(), fooAlias.getClazz());
        assertTrue(fooAlias.getColumn2field().keySet().contains("foo.STRING_FIELD"));
        assertFalse(fooAlias.getColumn2field().keySet().contains("STRING_FIELD"));

        PersistenceInfo<TestClass> barAlias = fooAlias.aliased("bar");
        assertTrue(barAlias.getColumn2field().keySet().contains("bar.STRING_FIELD"));
        assertFalse(barAlias.getColumn2field().keySet().contains("STRING_FIELD"));
    }
}
