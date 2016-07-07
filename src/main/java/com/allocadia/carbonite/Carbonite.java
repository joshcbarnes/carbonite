package com.allocadia.carbonite;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.allocadia.carbonite.transaction.CarboniteTransactionManager;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

import lombok.Data;
import lombok.NonNull;

@Data
public class Carbonite implements InitializingBean {

    @NonNull
    private CarboniteTransactionManager txManager;
    
    private Map<Class<?>, PersistenceInfo<?>> fieldCache;
    
    public CarboniteObjectManager getObjectManager() {
        return (CarboniteObjectManager) TransactionSynchronizationManager.getResource(txManager.getDataSource());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ImmutableMap.Builder<Class<?>, PersistenceInfo<?>> builder = ImmutableMap.builder();

        new ClassScanner().getPersistenceInfo().forEach(p -> builder.put(p.getClazz(), p));

        fieldCache = builder.build();
    }

    @SuppressWarnings("unchecked")
    public <T> PersistenceInfo<T> getPersistenceInfo(Class<T> resultClass) {
        return (PersistenceInfo<T>) Objects.requireNonNull(fieldCache.get(resultClass));
    }
}
