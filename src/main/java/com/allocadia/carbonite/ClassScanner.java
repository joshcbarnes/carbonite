package com.allocadia.carbonite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.allocadia.carbonite.annotation.Carbonated;
import com.allocadia.carbonite.annotation.Persist;

public class ClassScanner {

    private static final String WHOLE_CLASSPATH = "";
    private static final boolean NO_DEFAULT_FILTERS = false;
    
    @SneakyThrows
    public Map<Class<?>, List<Field>> getFieldMap() {
        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(NO_DEFAULT_FILTERS);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Carbonated.class));

        Map<Class<?>, List<Field>> fieldMap = new HashMap<>();
        ClassLoader classLoader = ClassScanner.class.getClassLoader();
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(WHOLE_CLASSPATH)) {
            String className = beanDefinition.getBeanClassName();
            Class<?> clazz = classLoader.loadClass(className);
            fieldMap.put(clazz, getFields(clazz));
        }
        
        return null;
    }

    private List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Persist.class)) {
                fields.add(field);
            }
        }
        return fields;
    }
}
