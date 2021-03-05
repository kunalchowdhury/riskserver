package com.quantlogic.stream;

public interface MarketDataStream<T> {

    MarketDataStream<T> merge(MarketDataStream<T>... streams);

    MarketDataStream<T> withWindow(IndexedMarketDataSink<T> window);


}
