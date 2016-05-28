package com.allocadia.carbonite;

import lombok.Data;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.allocadia.carbonite.transaction.CarboniteTransactionManager;

@Data
public class Carbonite {

    private CarboniteTransactionManager txManager;
    
    public CarboniteObjectManager getObjectManager() {
        return (CarboniteObjectManager) TransactionSynchronizationManager.getResource(txManager.getDataSource());
    }
}
