package com.allocadia.carbonite.transaction;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.allocadia.carbonite.CarboniteObjectManager;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarboniteJdoTransactionManagerShim extends JdoTransactionManager {

    private static final String SUSPENDED_OBJECT_MANAGERS = "suspended object managers";
    
    private CarboniteTransactionManager carboniteTM;

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        carboniteTM.doBegin(transaction, definition);
        
        @SuppressWarnings("unchecked")
        Map<Object, CarboniteObjectManager> suspendedObjectManagers = (Map<Object, CarboniteObjectManager>) TransactionSynchronizationManager.getResource(SUSPENDED_OBJECT_MANAGERS);
        if (suspendedObjectManagers == null) {
            TransactionSynchronizationManager.bindResource(SUSPENDED_OBJECT_MANAGERS, new HashMap<>());
        }
        
        super.doBegin(transaction, definition);
    }
    
    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        carboniteTM.doCommit(status);
        super.doCommit(status);
    }
    
    @Override
    protected Object doSuspend(Object transaction) {
        CarboniteObjectManager om = (CarboniteObjectManager) carboniteTM.doSuspend(transaction);
        
        @SuppressWarnings("unchecked")
        Map<Object, CarboniteObjectManager> suspendedObjectManagers = (Map<Object, CarboniteObjectManager>) TransactionSynchronizationManager.getResource(SUSPENDED_OBJECT_MANAGERS);

        suspendedObjectManagers.put(transaction, om);
        
        TransactionSynchronizationManager.bindResource(getDataSource(), carboniteTM.newObjectManager());
        
        return super.doSuspend(transaction);
    }
    
    @Override
    protected void doResume(Object transaction, Object suspendedResources) {
        @SuppressWarnings("unchecked")
        Map<Object, CarboniteObjectManager> suspendedObjectManagers = (Map<Object, CarboniteObjectManager>) TransactionSynchronizationManager.getResource(SUSPENDED_OBJECT_MANAGERS);
        
        CarboniteObjectManager om = suspendedObjectManagers.remove(transaction);
        
        carboniteTM.doResume(transaction, om);
        super.doResume(transaction, suspendedResources);
    }
    
    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        carboniteTM.doRollback(status);
        super.doRollback(status);
    }
}
