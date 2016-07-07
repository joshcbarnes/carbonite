package com.allocadia.carbonite;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.allocadia.carbonite.annotation.Carbonated;
import com.allocadia.carbonite.annotation.Persist;
import com.allocadia.carbonite.utils.QueryUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return new PersistenceInfo<>(clazz, getFields(clazz));
    }

    private static Map<String, Field> getFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Persist.class))
            .collect(Collectors.toMap(
                field -> {
                    Persist p = field.getAnnotation(Persist.class);

                    if (!p.column().isEmpty()) {
                        return p.column();
                    }

                    return QueryUtils.camel2underscore(field.getName());
                },
                Function.identity()
            ));
    }
}
