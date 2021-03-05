package com.quantlogic.stream;

public class PublisherConfig {

    static TimedCalculableStreamPipeline createCalculableStream(MarketDataType type){
        return new TimedCalculableStreamPipeline(null, type){

            @Override
            public IndexedMarketDataSink<Long> sinkFrom(IndexedMarketDataSink<Long> sink) {
                return null;
            }
        };
    }
}
