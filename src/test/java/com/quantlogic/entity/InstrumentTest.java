package com.quantlogic.entity;

import com.quantlogic.enumtype.DayCount;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class InstrumentTest {
    public static void main(String[] args) {
        Map<String, String> functions = Arrays.stream(VanillaOption.class.getDeclaredMethods())
                .collect(Collectors.toMap(f -> f.getName().replace("get", "").toLowerCase(),
                        f -> f.getName()+"()"));
        System.out.println(functions);

        System.out.println(Double.valueOf("12").equals(12.0));
        System.out.println(DayCount.ACTUAL_360.equals(DayCount.valueOf("ACTUAL_360")));
    }
}
