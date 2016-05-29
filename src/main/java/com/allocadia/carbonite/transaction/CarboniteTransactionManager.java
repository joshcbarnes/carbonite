package com.allocadia.carbonite.transaction;

import javax.sql.DataSource;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.allocadia.carbonite.CarboniteObjectManager;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarboniteTransactionManager extends AbstractPlatformTransactionManager {

    private DataSource dataSource;
    
    @Override
    protected Object doGetTransaction() throws TransactionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        TransactionSynchronizationManager.bindResource(getOMKey(), newObjectManager());
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        TransactionSynchronizationManager.unbindResource(getOMKey());
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        TransactionSynchronizationManager.unbindResource(getOMKey());
    }
    
    @Override
    protected Object doSuspend(Object transaction) throws TransactionException {
        return TransactionSynchronizationManager.unbindResource(getOMKey());
    }
    
    @Override
    protected void doResume(Object transaction, Object suspendedResources) throws TransactionException {
        TransactionSynchronizationManager.unbindResource(getOMKey());
        TransactionSynchronizationManager.bindResource(getOMKey(), suspendedResources);
    }

    private Object getOMKey() {
        return getDataSource().toString();
    }
    
    protected CarboniteObjectManager newObjectManager() {
        return new CarboniteObjectManager(dataSource);
    }
}
