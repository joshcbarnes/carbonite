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

public class ClassScannerTest {
    private static final class NonCarbonatedClassWithId {
        @Id
        private Integer foo;
    }
    @Test
    public void shouldWorkOnAnyClass_asLongAsItHasAnId() {
        ClassScanner.createPersistentInfo(NonCarbonatedClassWithId.class);
        ClassScanner.createPersistentInfo(TestClass.class);
    }

    @Test
    public void shouldReadPersistFieldsAndIgnoreOthers() {
        Map<String, Field> persistentFields = ClassScanner.createPersistentInfo(TestClass.class).getColumn2field();

        assertFalse(persistentFields.isEmpty());
        assertTrue(TestClass.class.getDeclaredFields().length > persistentFields.size());
    }

    @Test
    public void shouldConvertColumnNamesToUPPER_CASE_byDefault() {
        Set<String> columnNames = ClassScanner.createPersistentInfo(TestClass.class).getColumn2field().keySet();
        assertTrue(columnNames.containsAll(Arrays.asList("STRING_FIELD", "INT_FIELD")));
    }

    @Test
    public void shouldReadPersistAnnotationColumnNameWhenPresent() {
        Set<String> columnNames = ClassScanner.createPersistentInfo(TestClass.class).getColumn2field().keySet();

        assertFalse(columnNames.contains("CUSTOM_COLUMN_NAME"));
        assertTrue(columnNames.containsAll(Arrays.asList("field_NAME")));
    }
    
    @Test
    public void shouldReadIdAnnotation() {
        PersistenceInfo<TestClass> pi = ClassScanner.createPersistentInfo(TestClass.class);

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
        PersistenceInfo<IdAnnotationTest> pi = ClassScanner.createPersistentInfo(IdAnnotationTest.class);
        
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
        ClassScanner.createPersistentInfo(MultipleIdAnnotationTest.class);
    }

    @Test(expected = Exception.class)
    public void shouldThrowWhithNoId() {
        ClassScanner.createPersistentInfo(String.class);
    }
    
    @Carbonated
    private static final class CamalizedIdTest {
        @Id
        private Integer idWithCamels;
    }
    @Test
    public void shouldConvertCamelCaseIdsTo_UPPER_UNDERSCORE() {
        assertEquals("ID_WITH_CAMELS", ClassScanner.createPersistentInfo(CamalizedIdTest.class).getIdField());
    }
}
