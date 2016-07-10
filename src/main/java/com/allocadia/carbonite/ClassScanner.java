package com.allocadia.carbonite;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.allocadia.carbonite.annotation.Carbonated;

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
            info.add(PersistenceInfo.from(clazz));
        }
        
        return info;
    }
}
