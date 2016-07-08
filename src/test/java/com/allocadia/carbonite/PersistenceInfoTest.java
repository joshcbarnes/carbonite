package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class PersistenceInfoTest {
    @Test
    public void shouldAliasOriginalName() throws Exception {
        PersistenceInfo<TestClass> pi = new PersistenceInfo<>(TestClass.class, ImmutableMap.of("STRING_FIELD", TestClass.class.getDeclaredField("stringField")), "ID");
        
        PersistenceInfo<TestClass> fooAlias = pi.aliased("foo");

        assertEquals(pi.getClazz(), fooAlias.getClazz());
        assertTrue(fooAlias.getColumn2field().keySet().contains("foo.STRING_FIELD"));
        assertFalse(fooAlias.getColumn2field().keySet().contains("STRING_FIELD"));

        PersistenceInfo<TestClass> barAlias = fooAlias.aliased("bar");
        assertTrue(barAlias.getColumn2field().keySet().contains("bar.STRING_FIELD"));
        assertFalse(barAlias.getColumn2field().keySet().contains("STRING_FIELD"));
    }
}
