package com.quantlogic.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

public class Window implements IndexedMarketDataSink<Long> {


    enum WindowType{
        ROLLING, TUMBLING
    }

    private final long millis;
    private final int sz;
    private final AtomicLongArray spots;
    private final AtomicLongArray vols;
    private final AtomicLongArray yieldCurve;
    private final WindowType windowType;
    private final AtomicInteger spotIdx;
    private final AtomicInteger volIdx;
    private final AtomicInteger yieldIdx;
    private final Collection<IndexedMarketDataSink<Long>> upstreamSources;

    private long snapTime ;

    static class WindowBuilder{

        private long millis;
        private int sz;
        private WindowType windowType;

        WindowBuilder withSnapTime(long millis){
            this.millis = millis;
            return this;
        }

        WindowBuilder withSizeOf(int sz){
            this.sz = sz;
            return this;
        }
        WindowBuilder withWindowType(WindowType windowType){
            this.windowType = windowType;
            return this;
        }

        Window build(){
            return new Window(sz, millis, windowType);
        }

    }

    Window(int sz, long millis, WindowType windowType) {
        spots = new AtomicLongArray(sz);
        vols = new AtomicLongArray(sz);
        yieldCurve = new AtomicLongArray(sz);
        this.windowType = windowType;
        this.spotIdx = new AtomicInteger(0);
        this.volIdx = new AtomicInteger(0);
        this.yieldIdx = new AtomicInteger(0);
        this.sz = sz;
        this.millis = millis;
        upstreamSources = new ArrayList<>();
    }

    public void addSpot(int curKey, int curVersion){
        long timestamp = Timer.INSTANCE.getCurrentTime();
        long val = (long) curKey << 48 | (long) curVersion << 32 | timestamp;
        spots.set(curKey % sz, val );
        movePointer(spotIdx);
    }
    public void addVol(int curKey, int curVersion){
        long timestamp = Timer.INSTANCE.getCurrentTime();
        long val = (long) curKey << 48 | (long) curVersion << 32 | timestamp;
        vols.set(curKey % sz, val );
        movePointer(volIdx);
    }

    public void addYieldCurve(int curKey, int curVersion){
        long timestamp = Timer.INSTANCE.getCurrentTime();
        long val = (long) curKey << 48 | (long) curVersion << 32 | timestamp;
        yieldCurve.set(curKey % sz, val );
        movePointer(yieldIdx);
    }

    private void movePointer(AtomicInteger idx) {
        int curIdx = idx.get();
        switch(windowType){
            case ROLLING:
                while(!idx.compareAndSet(curIdx, moveRollingPointerAhead(curIdx))){
                    curIdx = idx.get();
                }
                break;
            case TUMBLING:
                while(!idx.compareAndSet(curIdx, moveTumblingPointerAhead(curIdx))){
                    curIdx = idx.get();
                }
                break;
        }
    }
    private int moveTumblingPointerAhead(int curIdx) {
        int left = curIdx >> 16;
        int right = curIdx & 0xFFFF;
        right = right + 1;
        if(right == sz -1 ){
            left = (left + 1) %sz;
        }
        right = right %sz;
        return left << 16 | right;
    }

    private int moveRollingPointerAhead(int curIdx) {
        int left = curIdx >> 16;
        int right = curIdx & 0xFFFF;
        right = (right + 1) % sz;
        return left << 16 | right;
    }

    @Override
    public void begin() {
        upstreamSources.forEach(src -> {
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                switch(src.getUnderlyingType()){
                    case SPOT:
                        while (spotIdx.get() <= src.getProducerIdx().get()){
                            AtomicLongArray dataArray = src.getDataArray();
                            long val = dataArray.get(spotIdx.get());
                            int key = (int) (val >> 32);
                            addSpot(key, (int)val);
                        }
                        break;
                    case VOL:
                        while (volIdx.get() <= src.getProducerIdx().get()){
                            AtomicLongArray dataArray = src.getDataArray();
                            long val = dataArray.get(volIdx.get());
                            int key = (int) (val >> 32);
                            addVol(key, (int)val);
                        }
                        break;
                    case YIELDCURVE:
                        while (yieldIdx.get() <= src.getProducerIdx().get()){
                            AtomicLongArray dataArray = src.getDataArray();
                            long val = dataArray.get(yieldIdx.get());
                            int key = (int) (val >> 32);
                            addYieldCurve(key, (int)val);
                        }
                        break;
                }

            }, millis, millis, TimeUnit.MILLISECONDS);
        });
   }

    @Override
    public void accept(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MarketDataType getUnderlyingType() {
        throw new UnsupportedOperationException("Cannot call this method on Window Type");
    }

    @Override
    public AtomicInteger getProducerIdx() {
        throw new UnsupportedOperationException("Cannot call this method on Window Type");
    }

    @Override
    public AtomicLongArray getDataArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void register(IndexedMarketDataSink<Long> other) {
        upstreamSources.add(other);
    }

    @Override
    public void end() {
       // calculate
        snap();
    }


    private void snap(){
        int currentTime = Timer.INSTANCE.getCurrentTime();

    }

    private static int indexOf(AtomicLongArray arr, long key){
        int lo = 0;
        int hi = arr.length() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int timestamp = (int)arr.get(mid) == 0 ? Integer.MAX_VALUE : (int)arr.get(mid) ;
            if      (key < timestamp) hi = mid - 1;
            else if (key > timestamp) lo = mid + 1;
            else return mid;
        }
        return -(lo +1);
    }
    public static WindowBuilder builder(){
        return new WindowBuilder();
    }

}
