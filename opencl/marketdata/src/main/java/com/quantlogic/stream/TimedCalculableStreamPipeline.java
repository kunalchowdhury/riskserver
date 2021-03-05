package com.quantlogic.stream;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TimedCalculableStreamPipeline extends CalculableStreamPipeline<Long> implements MarketDataStream<Long>{

    private final Collection<CalculableStreamPipeline<Long>> collection;
    private final MarketDataType sourceType;
    private IndexedMarketDataSink<Long> window;
    private final Set<ExecutorService> services;

    public TimedCalculableStreamPipeline(CalculableStreamPipeline<Long> prev, MarketDataType type) {
        super(prev);
        this.collection = new LinkedList<>();
        this.sourceType = type;
        this.services = new HashSet<>();

    }

    @Override
    public void evaluatePipeline() {
        window.end();
    }

    @Override
    public MarketDataStream<Long> merge(MarketDataStream<Long>... streams) {

        for (MarketDataStream<Long> stream : streams) {
            if(stream instanceof TimedCalculableStreamPipeline) {
                TimedCalculableStreamPipeline curStream = (TimedCalculableStreamPipeline) stream;
                TimedCalculableStreamPipeline timedCalculableStreamPipeline = new TimedCalculableStreamPipeline(
                        curStream, curStream.sourceType) {
                    @Override
                    public IndexedMarketDataSink<Long> sinkFrom(IndexedMarketDataSink<Long> sink) {
                        return new VanillaIndexedMarketDataSink(32, sink, curStream.sourceType);
                    }
                };
                collection.add(timedCalculableStreamPipeline);
            }
        }

        collection.add(new TimedCalculableStreamPipeline(this, sourceType) {
            @Override
            public IndexedMarketDataSink<Long> sinkFrom(IndexedMarketDataSink<Long> sink) {
                return new VanillaIndexedMarketDataSink(32, sink, sourceType);
            }
        });

        return this;
    }


    @Override
    public MarketDataStream<Long> withWindow(IndexedMarketDataSink<Long> window) {
        if(!(window instanceof Window)){
            throw new IllegalStateException("Passed window not instance of Window");
        }
        this.window = window;
        this.collection.forEach(c -> {
            IndexedMarketDataSink<Long> longIndexedMarketDataSink = c.sinkFrom(window);
            registerRedisListener(longIndexedMarketDataSink);
            longIndexedMarketDataSink.begin();
        });
        evaluatePipeline();
        return this;
    }

    protected void registerRedisListener(IndexedMarketDataSink<Long> longIndexedMarketDataSink){
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        newSingleThreadExecutor.submit(() -> {
            JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
            Jedis jedis = pool.getResource();
            jedis.psubscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    String[] components = message.split(":");
                    if(MarketDataType.valueOf(components[0])
                            == longIndexedMarketDataSink.getUnderlyingType() ) {
                        longIndexedMarketDataSink.accept(Long.valueOf(jedis.hget(components[0], components[1])));
                    }
                }
            }, "__key*__:*");
        });

        services.add(newSingleThreadExecutor);
    }

    static class RootPipeline extends TimedCalculableStreamPipeline{

        public RootPipeline(TimedCalculableStreamPipeline prev, MarketDataType type) {
            super(prev, type);
        }

        @Override
        public IndexedMarketDataSink<Long> sinkFrom(IndexedMarketDataSink<Long> sink) {
            throw new UnsupportedOperationException();
        }
    }


}
