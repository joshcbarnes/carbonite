package com.allocadia.carbonite.utils;

public class QueryUtils {

    public static String camel2underscore(String name) {
        return name.replaceAll("([^_A-Z])([A-Z]+)", "$1_$2").toUpperCase();
    }
}
