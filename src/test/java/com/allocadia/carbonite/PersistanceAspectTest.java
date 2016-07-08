package com.allocadia.carbonite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Set;

public class PersistanceAspectTest {
    @Test
    public void shouldAddThePOInterface() {
        assertTrue(new TestClass() instanceof PersistedObject);
        assertTrue(((PersistedObject)new TestClass()).getDirty() instanceof Set);
    }

    @Test
    public void shouldMarkFieldsAsDirtyOnSet() {
        TestClass obj = new TestClass();
        PersistedObject po = (PersistedObject)obj;

        assertEquals(0, po.getDirty().size());

        obj.setId(123);
        assertEquals(1, po.getDirty().size());

        obj.setId(456);
        assertEquals(1, po.getDirty().size());

        obj.setStringField("foo");
        assertEquals(2, po.getDirty().size());

        obj.setStringField("bar");
        assertEquals(2, po.getDirty().size());

        obj.publicField = null;
        assertEquals(3, po.getDirty().size());

        obj.publicField = "bar";
        assertEquals(3, po.getDirty().size());
    }

    @Test
    public void shouldNotMarkFieldsAsDirtyOnGet() {
        TestClass obj = new TestClass();
        PersistedObject po = (PersistedObject)obj;
        
        assertEquals(0, po.getDirty().size());

        obj.getId();
        assertEquals(0, po.getDirty().size());

        obj.getStringField();
        assertEquals(0, po.getDirty().size());

        String tmp = obj.publicField;
        assertEquals(null, tmp);
        assertEquals(0, po.getDirty().size());
    }
}
