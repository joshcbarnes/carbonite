package com.allocadia.carbonite;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ClassScannerTest {
    @Test
    public void shouldWorkOnAnyClass() {
        ClassScanner.createPersistentInfo(String.class);
        ClassScanner.createPersistentInfo(Object.class);
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
}
