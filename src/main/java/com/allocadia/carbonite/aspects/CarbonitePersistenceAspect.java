package com.allocadia.carbonite.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;

import com.allocadia.carbonite.PersistedObject;

@Aspect
public class CarbonitePersistenceAspect extends PersistanceAspect {
    @Override
    @Pointcut("set(@com.allocadia.carbonite.annotation.Id * *) || set(@com.allocadia.carbonite.annotation.Persist * *)")
    public void fieldSetters() {
    }

    @DeclareParents(value="(@com.allocadia.carbonite.annotation.Carbonated *)", defaultImpl = PersistedObjectImpl.class)
    private PersistedObject implementedInterface;
}