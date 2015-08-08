package com.allocadia.carbonite;

import lombok.Data;

import com.allocadia.carbonite.annotation.Persist;

@Data
public class TestClass {
    @Persist
    private String field1;
    @Persist
    private int field2;
}
