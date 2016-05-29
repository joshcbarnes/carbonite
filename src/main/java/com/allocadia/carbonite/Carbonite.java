package com.allocadia.carbonite;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NonNull;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.allocadia.carbonite.transaction.CarboniteTransactionManager;

@Data
public class Carbonite implements InitializingBean {

    @NonNull
    private CarboniteTransactionManager txManager;
    
    private Map<Class<?>, List<Field>> fieldCache;
    
    public CarboniteObjectManager getObjectManager() {
        return (CarboniteObjectManager) TransactionSynchronizationManager.getResource(txManager.getDataSource());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fieldCache = new ClassScanner().getFieldMap();
    }
}
