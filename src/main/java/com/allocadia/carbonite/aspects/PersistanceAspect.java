package com.allocadia.carbonite.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.allocadia.carbonite.PersistedObject;

import java.util.HashSet;
import java.util.Set;

@Aspect
public abstract class PersistanceAspect {
    public static final class PersistedObjectImpl implements PersistedObject {
        private final Set<String> dirty = new HashSet<>();
        private State state = State.NEW;

        @Override
        public Set<String> getDirty() {
            return dirty;
        }

        @Override
        public void markDirty(String fieldName) {
            dirty.add(fieldName);
            if (state == State.CLEAN) {
                state = State.DIRTY;
            }
        }

        @Override
        public void markClean() {
            state = State.CLEAN;
            dirty.clear();
        }

        @Override
        public State getState() {
            return state;
        }
    }

    @Pointcut
    public abstract void fieldSetters();

    @After("fieldSetters() && target(po)")
    public void markFieldDirty(JoinPoint.StaticPart jp, PersistedObject po) {
        po.markDirty(jp.getSignature().getName());
    }
}