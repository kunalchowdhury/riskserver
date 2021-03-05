package com.quantlogic.stream;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

public class VanillaIndexedMarketDataSink implements IndexedMarketDataSink<Long> {

    private final MarketDataType marketDataType;
    private VanillaIndexedMarketDataSink dataSink;
    private final long startAddress;
    private AtomicLongArray data;
    private final int elements;
    private int index;
    private AtomicInteger curProducerIdx;
    private int offHeapIdx;
    private static Unsafe unsafe;

    static {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);

        }
    }
    public VanillaIndexedMarketDataSink(int elements,
                                        IndexedMarketDataSink<Long> sink,
                                        MarketDataType marketDataType) {
        this.elements = elements ;
        this.startAddress = unsafe.allocateMemory(8L * elements);
        this.offHeapIdx = 0;
        this.marketDataType = marketDataType;
        sink.register(this);

    }

    @Override
    public void begin() {
        this.data = new AtomicLongArray(elements);
        for (int i = 0; i < elements; i++) {
            data.set(i, -1);
        }
        this.index = 0;
        this.curProducerIdx = new AtomicInteger(0);
    }

    @Override
    public void accept(Long aLong) {
        if(offHeapIdx > 0){
            int curIdx = this.offHeapIdx;
            for (int i = 0; i < curIdx; i++) {
                long offLong = unsafe.getLong(startAddress + i * 8L);
                int idx = (index++) % elements;
                boolean success = data.compareAndSet(idx, -1, offLong);
                if (success) {
                    curProducerIdx.set(idx);
                    offHeapIdx--;
                }else {
                    index--;
                    break;
                }
            }
            unsafe.putLong(startAddress + 8L * offHeapIdx, aLong);
            offHeapIdx = (offHeapIdx + 1) % elements;

        }else {
            int idx = (index++) % elements;
            boolean success = data.compareAndSet(idx, -1, aLong);
            if (success) {
                curProducerIdx.set(idx);
            } else {
                unsafe.putLong(startAddress + 8L * offHeapIdx, aLong);
                offHeapIdx = (offHeapIdx + 1) % elements;
                index--;
            }
        }
    }

    @Override
    public MarketDataType getUnderlyingType() {
        return marketDataType;
    }

    @Override
    public AtomicInteger getProducerIdx() {
        return curProducerIdx;
    }

    @Override
    public AtomicLongArray getDataArray() {
        return data;
    }

    @Override
    public void register(IndexedMarketDataSink<Long> other) { }

    @Override
    public void end() {
        for (int i = 0; i < this.offHeapIdx; i++) {
            long offLong = unsafe.getLong(startAddress + i * 8L);
            int idx = (index++) % elements;
            data.set(idx, offLong);
            offHeapIdx--;
        }
    }
}
