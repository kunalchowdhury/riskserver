package com.quantlogic.stream;

public abstract class CalculableStreamPipeline<T> {
    protected final CalculableStreamPipeline<T> prev;

    public CalculableStreamPipeline(CalculableStreamPipeline<T> prev) {
        this.prev = prev;
    }

    public abstract void evaluatePipeline();

    public abstract IndexedMarketDataSink<T> sinkFrom(IndexedMarketDataSink<T> sink);


}
