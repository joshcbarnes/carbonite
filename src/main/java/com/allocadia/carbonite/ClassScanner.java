package com.allocadia.carbonite;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.allocadia.carbonite.annotation.Carbonated;
import com.allocadia.carbonite.annotation.Id;
import com.allocadia.carbonite.annotation.Persist;
import com.allocadia.carbonite.utils.QueryUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.SneakyThrows;

public class ClassScanner {

    private static final String WHOLE_CLASSPATH = "";
    private static final boolean NO_DEFAULT_FILTERS = false;
    
    @SneakyThrows
    public Set<PersistenceInfo<?>> getPersistenceInfo() {
        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(NO_DEFAULT_FILTERS);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Carbonated.class));

        ClassLoader classLoader = ClassScanner.class.getClassLoader();

        Set<PersistenceInfo<?>> info = new HashSet<>();
        
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(WHOLE_CLASSPATH)) {
            String clazzName = beanDefinition.getBeanClassName();
            Class<?> clazz = classLoader.loadClass(clazzName);
            info.add(createPersistentInfo(clazz));
        }
        
        return info;
    }

    public static <T> PersistenceInfo<T> createPersistentInfo(Class<T> clazz) {
        return new PersistenceInfo<>(clazz, getFields(clazz), getIdField(clazz));
    }

    private static String getIdField(Class<?> clazz) {
        return Iterators.getOnlyElement(
            Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(field -> {
                    Persist p = field.getAnnotation(Persist.class);
                    if (null != p && !p.column().isEmpty()) {
                        return p.column();
                    }
    
                    return QueryUtils.camel2underscore(field.getName());
                })
                .iterator()
        );
    }

    private static ImmutableMap<String, Field> getFields(Class<?> clazz) {
        ImmutableMap.Builder<String, Field> builder = ImmutableMap.builder();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Persist.class) || field.isAnnotationPresent(Id.class)) {
                final String name;

                final Persist p = field.getAnnotation(Persist.class);
                if (null != p && !p.column().isEmpty()) {
                    name = p.column();
                } else {
                    name = QueryUtils.camel2underscore(field.getName());
                }

                builder.put(name, field);
            }
        }

        return builder.build();
    }
}
