package com.quantlogic.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.quantlogic.annotation.RuleUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ReflectionUtils {

    public static final ReflectionUtils INSTANCE = new ReflectionUtils();
    private static final Map<Class<?>, Function<?, String>> canonName = Maps.newHashMap();

    static {
        canonName.put(Boolean.class, (Function<String, String>) s -> "Boolean.valueOf(\""+s+"\")");
        canonName.put(Byte.class, (Function<String, String>) s -> "Byte.valueOf(\""+s+"\")");
        canonName.put(Enum.class, (Function<Pair<String, String>, String>) s -> s.getLeft()+".valueOf(\""+s.getRight()+"\")");
        canonName.put(Integer.class, (Function<String, String>) s -> "Integer.valueOf(\""+s+"\")");
        canonName.put(Float.class, (Function<String, String>) s -> "Float.valueOf(\""+s+"\")");
        canonName.put(Long.class, (Function<String, String>) s -> "Long.valueOf(\""+s+"\")");
        canonName.put(Double.class, (Function<String, String>) s -> "Double.valueOf(\""+s+"\")");
        canonName.put(String.class, (Function<String, String>) s -> "String.valueOf(\""+s+"\")");
    }

    private final Map<Class<?>, Map<String, Method>> entityGetterMap = Maps.newConcurrentMap();
    private final ThreadLocal<StringBuilder> EQUALS_BUILDER = new ThreadLocal<StringBuilder>(){
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };
    private final Map<String, Set<Method>> methodMap ;

    private ReflectionUtils() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RuleUtil.class));
        scanner.clearCache();
        Set<BeanDefinition> definitions = scanner.findCandidateComponents("com.quantlogic");
        this.methodMap = definitions.stream().collect(Collectors.toMap(BeanDefinition::getBeanClassName, b -> {
            try {
                return Sets.newHashSet(Class.forName(b.getBeanClassName()).getDeclaredMethods());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return Sets.newHashSet();
        }));
    }

    public Method getMethodName(Class<?> clsName, String fldName){
        entityGetterMap.putIfAbsent(clsName,
                Arrays.stream(clsName.getDeclaredMethods())
                .collect(Collectors.toMap(
                        method -> method.getName().substring(method.getName().lastIndexOf(".") + 1).replace("get", "").toLowerCase(),
                        method -> method
                        )
                ));
        return entityGetterMap.get(clsName).get(fldName.toLowerCase());
    }

    public Class<?> getReturnType(Class<?> clsName, String fldName){
        return getMethodName(clsName, fldName).getReturnType();
    }

    public String generateEqualsStatement(Class<?> clsName, String curInstanceName, String stmt){
        String[] fields = stmt.split("==");
        String targetFldName = fields[1].trim();
        Method methodName = getMethodName(clsName, fields[0].replace("$", "").trim());
        StringBuilder sb = EQUALS_BUILDER.get();
        sb.delete(0, sb.length());
        sb.append(curInstanceName).append(".").append(methodName.getName()).append("()").append(".").append("equals(");
        String returnValue ;
        if(methodName.getReturnType() != Enum.class){
            Function<String, String> stringFunction = (Function<String, String>) canonName.get(methodName.getReturnType());
            returnValue = stringFunction.apply(targetFldName);
        }else {
            Function<Pair<String, String>, String> enumFunction =
                    (Function<Pair<String, String>, String>) canonName.get(methodName.getReturnType());
            returnValue = enumFunction.apply(Pair.of(methodName.getReturnType().getCanonicalName(), targetFldName));
        }
        sb.append(returnValue).append(")");
        return sb.toString();
    }

    private Optional<String> findNearestClassNameMatch(String name){
        return this.methodMap.keySet().stream().filter(name::startsWith).findFirst();
    }

    public Optional<String> generateMethodCallStatement(String command) throws Exception {
        this.methodMap.get("");
        if(StringUtils.contains(command, ".") && findNearestClassNameMatch(command).isPresent()){
            String clsName = findNearestClassNameMatch(command).get();
            command = command.replaceFirst(clsName + ".", "");
            Class<?> aClass = Class.forName(clsName);
            String methodName = command.substring(0, command.indexOf('('));
            Optional<Method> method = Arrays.stream(aClass.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst();
            if(method.isPresent()){
                String[] params = command.substring(command.indexOf('(') +1 , command.indexOf(")")).split(",");
                Parameter[] parameters = method.get().getParameters();
                StringBuilder sb = new StringBuilder(clsName).append(methodName).append(".(");
                if(params.length == parameters.length){
                    IntStream.range(0 , parameters.length).forEach(i -> {
                        if(parameters[i].getType() != Enum.class){
                            Function<String, String> stringFunction = (Function<String, String>) canonName.get(parameters[i].getType());
                            sb.append(stringFunction.apply(params[i].trim()));
                        }else {
                            Function<Pair<String, String>, String> enumFunction =
                                    (Function<Pair<String, String>, String>) canonName.get(parameters[i].getType());
                            sb.append(enumFunction.apply(Pair.of(parameters[i].getType().getCanonicalName(), params[i].trim())));
                        }
                        if(i < parameters.length-1) {
                            sb.append(",");
                        }
                    });
                }
               return Optional.of(sb.append(")").toString());
            }
        }
        return Optional.empty();
    }


}
