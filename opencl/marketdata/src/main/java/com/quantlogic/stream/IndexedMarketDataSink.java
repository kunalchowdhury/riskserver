package com.quantlogic.stream;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

public interface IndexedMarketDataSink<T> {
    void begin();
    void accept(T t);
    MarketDataType getUnderlyingType();
    AtomicInteger getProducerIdx();
    AtomicLongArray getDataArray();
    void register(IndexedMarketDataSink<T> other);
    void end();
}
