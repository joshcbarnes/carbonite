package com.allocadia.carbonite.utils;

public class QueryUtils {

    public static String camel2underscore(String name) {
        return name.replaceAll("([^_A-Z])([A-Z]+)", "$1_$2").toUpperCase();
    }
    
    public static String createParamList(int size) {
        String paramList = "";
        for (int i = 0; i < size - 1; i++) {
            paramList += "?, ";
        }
        paramList += "?";
        return paramList;
    }
}
