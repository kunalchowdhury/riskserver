package com.quantlogic.entity.metadata;

import com.google.common.collect.Maps;
import com.quantlogic.annotation.BaseEntity;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityMetadataRepository {

    public static EntityMetadataRepository INSTANCE = new EntityMetadataRepository();
    private final Map<String, Map<String , Class<?>>> functionMap;

    private EntityMetadataRepository() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(BaseEntity.class));
        Set<BeanDefinition> definitions = scanner.findCandidateComponents("com.quantlogic");
        this.functionMap =
                definitions.stream().collect(Collectors.toMap(BeanDefinition::getBeanClassName, b -> {
            try {
                return Arrays.stream(Class.forName(b.getBeanClassName()).getDeclaredMethods())
                        .filter(m -> m.getName()
                                .startsWith("get")).collect(Collectors.toMap(k -> k.getName()
                                .replaceAll("get", "").toLowerCase(), Method::getReturnType));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return Maps.newHashMap();
        }));
    }

    public Map<String, Map<String, Class<?>>> getFunctionMap() {
        return functionMap;
    }
}
