package com.allocadia.carbonite.exception;

public class CarboniteException extends RuntimeException {

    private static final long serialVersionUID = -7499116494757990117L;
    
    public CarboniteException(String msg) {
        super(msg);
    }
}
