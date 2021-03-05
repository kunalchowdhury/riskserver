package com.quantlogic.stream;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

public class IndexedMDSinkNode implements IndexedMarketDataSink<Long>{

    private final IndexedMarketDataSink<Long> next;
    private final MarketDataType marketDataType;

    public IndexedMDSinkNode(IndexedMarketDataSink<Long> next, MarketDataType marketDataType) {
        this.next = next;
        this.marketDataType = marketDataType;
    }

    @Override
    public void begin() {
        next.begin();
    }

    @Override
    public void accept(Long aLong) {
        next.accept(aLong);
    }

    @Override
    public MarketDataType getUnderlyingType() {
        return marketDataType;
    }


    @Override
    public AtomicInteger getProducerIdx() {
        return next.getProducerIdx();
    }

    @Override
    public AtomicLongArray getDataArray() {
        return null;
    }

    @Override
    public void register(IndexedMarketDataSink<Long> other) {

    }

    @Override
    public void end() {
        next.end();
    }
}
