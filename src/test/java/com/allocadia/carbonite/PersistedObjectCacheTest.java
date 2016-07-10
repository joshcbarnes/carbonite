package com.allocadia.carbonite;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PersistedObjectCacheTest {
    private static TestClass createTestClass(Integer id) {
        TestClass t = new TestClass();
        t.setId(id);
        return t;
    }

    @Test
    public void shouldReturnNullWhenNotFound() {
        assertNull(new PersistedObjectCache().getObjectById(TestClass.class, 123));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenPassedNullId() {
        assertNull(new PersistedObjectCache().getObjectById(TestClass.class, null));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenPassedNullId2() {
        assertNull(new PersistedObjectCache().getObjectById(TestClass.class, null, PersistedObjectCacheTest::createTestClass));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFactoryReturnsNull() {
        assertNull(new PersistedObjectCache().getObjectById(TestClass.class, 123, x -> null));
    }

    @Test
    public void shouldFallbackToFactoryWhenNotFound() {
        PersistedObjectCache pom = new PersistedObjectCache();

        assertNull(pom.getObjectById(TestClass.class, 123));

        boolean[] wasCalled = new boolean[2];

        TestClass obj1 = pom.getObjectById(TestClass.class, 123, x -> {
            wasCalled[0] = true;
            return PersistedObjectCacheTest.createTestClass(x); 
        });
        assertTrue(wasCalled[0]);

        TestClass obj2 = pom.getObjectById(TestClass.class, 123, x -> {
            wasCalled[1] = true;
            return PersistedObjectCacheTest.createTestClass(x); 
        });
        assertFalse(wasCalled[1]);
        
        assertTrue(obj1 == obj2);
    }
}
