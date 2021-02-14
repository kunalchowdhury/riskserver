package com.quantlogic.snapshotorchestrator;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;

public class Test {
    private static Collection<String> get(String id){
        Set<String> s = new HashSet<>();
        s.add("A");
        s.add("B");
        return s;
    }
    private static int startAdd(String k){
        return new Random().nextInt(100);
    }

    public static void main(String[] args) {
        String tags = "a,b,a";
        Map<String, Set<Integer>> collect = Arrays.stream(tags.split(",")).map(key ->
                get(key).stream().map(address -> Pair.of(key, startAdd(address)))
        ).flatMap(Stream::sorted).collect(Collectors.groupingBy(Pair::getLeft, mapping(Pair::getRight, Collectors.toSet())));

        System.out.println(collect);

    }
}
