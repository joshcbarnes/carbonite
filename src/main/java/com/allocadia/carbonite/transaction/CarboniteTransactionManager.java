package com.allocadia.carbonite.transaction;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.allocadia.carbonite.CarboniteObjectManager;

import javax.sql.DataSource;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarboniteTransactionManager extends AbstractPlatformTransactionManager {
    public static final String OM_KEY = CarboniteObjectManager.class.getName();
    
    private DataSource dataSource;
    
    @Override
    protected Object doGetTransaction() throws TransactionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        TransactionSynchronizationManager.bindResource(OM_KEY, newObjectManager());
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        TransactionSynchronizationManager.unbindResource(OM_KEY);
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        TransactionSynchronizationManager.unbindResource(OM_KEY);
    }
    
    @Override
    protected Object doSuspend(Object transaction) throws TransactionException {
        return TransactionSynchronizationManager.unbindResource(OM_KEY);
    }
    
    @Override
    protected void doResume(Object transaction, Object suspendedResources) throws TransactionException {
        TransactionSynchronizationManager.unbindResource(OM_KEY);
        TransactionSynchronizationManager.bindResource(OM_KEY, suspendedResources);
    }

    protected CarboniteObjectManager newObjectManager() {
        return new CarboniteObjectManager(dataSource);
    }
}
