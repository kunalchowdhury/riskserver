package com.quantlogic.stream;

import java.util.Arrays;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StreamTest {

    public static void main(String[] args) {
        LongStream longStream = LongStream.of(10, 20, 30, 40);
       // Spliterator.OfLong spliterator = longStream.spliterator();
      /*  while (spliterator.tryAdvance((LongConsumer) System.out::println)){
            System.out.println("going ahead");
        }*/
      /*  OptionalLong any = longStream
                .map(l -> l -1)
                .filter(new LongPredicate() {
            @Override
            public boolean test(long value) {
                return value > 0;
            }
        }).findAny();*/

        longStream
                .map(new LongUnaryOperator() {
                    @Override
                    public long applyAsLong(long in) {
                        return in * 100;
                    }
                }).filter(new LongPredicate() {
                    @Override
                    public boolean test(long value) {
                        return value > 0;
                    }
                }).forEach(new LongConsumer() {
                    @Override
                    public void accept(long value) {
                        System.out.println(value);

                    }
                });

    }
}
