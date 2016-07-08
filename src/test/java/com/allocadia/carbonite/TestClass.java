package com.allocadia.carbonite;

import com.allocadia.carbonite.annotation.Carbonated;
import com.allocadia.carbonite.annotation.Id;
import com.allocadia.carbonite.annotation.Persist;

import lombok.Data;

@Data
@Carbonated
public class TestClass {
    @Id
    private Integer id;

    @Persist
    private String stringField;
    @Persist
    private int intField;
    @Persist
    private Integer integerField;
    @Persist(column = "field_NAME")
    private String customColumnName;
    @Persist
    public String publicField;

    private String nonPersisted;
}
