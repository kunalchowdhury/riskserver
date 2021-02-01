package com.quantlogic.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ReflectionUtils {

    private static final Map<Class<?>, Map<String, Method>> entityGetterMap = Maps.newConcurrentMap();
    private static final Map<Class<?>, Function<?, String>> canonName = Maps.newHashMap();
    static {
        canonName.put(Boolean.class, (Function<String, String>) s -> "Boolean.valueOf("+s+")");
        canonName.put(Byte.class, (Function<String, String>) s -> "Byte.valueOf("+s+")");
        canonName.put(Enum.class, (Function<Pair<String, String>, String>) s -> s.getLeft()+".valueOf("+s.getRight()+")");
        canonName.put(Integer.class, (Function<String, String>) s -> "Integer.valueOf("+s+")");
        canonName.put(Float.class, (Function<String, String>) s -> "Float.valueOf("+s+")");
        canonName.put(Long.class, (Function<String, String>) s -> "Long.valueOf("+s+")");
        canonName.put(Double.class, (Function<String, String>) s -> "Double.valueOf("+s+")");
        canonName.put(String.class, (Function<String, String>) s -> "String.valueOf("+s+")");
    }

    public static Method getMethodName(Class<?> clsName, String fldName){
        entityGetterMap.putIfAbsent(clsName,
                Arrays.stream(clsName.getDeclaredMethods())
                .collect(Collectors.toMap(
                        method -> method.getName().replace("get", "").toLowerCase(),
                        method -> method
                        )
                ));
        return entityGetterMap.get(clsName).get(fldName);
    }

    public static Class<?> getReturnType(Class<?> clsName, String fldName){
        return getMethodName(clsName, fldName).getReturnType();
    }

    public static String generateEqualsStatement(Class<?> clsName, String curInstanceName, String fldName, String targetFldName){
        Method methodName = getMethodName(clsName, fldName);
        StringBuilder sb = new StringBuilder(curInstanceName)
                .append(".")
                .append(methodName.getName())
                .append("equals(");

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


}
