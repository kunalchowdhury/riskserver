package com.quantlogic.snapshot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositeSnapshotWindow {

    private long snaptime;
    private final Cache<String, Integer> spotsnap;
    private final Cache<String, Integer> volsnap;
    private final Cache<String, Integer> yieldCurveSnap;
    private final Set<String> spotKeys;
    private final Set<String> volKeys;
    private final Set<String> yieldCurveKeys;
    private final Map<String, Integer> bufferedSpotSnap;
    private final Map<String, Integer> bufferedVolsnap;
    private final Map<String, Integer> bufferedYieldCurveSnap;


    public CompositeSnapshotWindow() {
        this.spotsnap = CacheBuilder.newBuilder().build();
        this.volsnap = CacheBuilder.newBuilder().build();
        this.yieldCurveSnap = CacheBuilder.newBuilder().build();
        this.spotKeys = Sets.newHashSet();
        this.volKeys = Sets.newHashSet();
        this.yieldCurveKeys = Sets.newHashSet();
        this.bufferedSpotSnap = new HashMap<>();
        this.bufferedVolsnap = new HashMap<>();
        this.bufferedYieldCurveSnap = new HashMap<>();
    }

    public void setSnaptime(long snaptime){
        this.snaptime = snaptime;
    }

    public void setSpotsnap(String key, int version, boolean isAddressUsed){
        if(!isAddressUsed) {
            this.spotKeys.add(key);
            this.spotsnap.put(key, version);
        }else {
            this.bufferedSpotSnap.put(key, version);
        }
    }

    public void setVolsnap(String key, int version,  boolean isAddressUsed){
        if(!isAddressUsed) {
            this.volKeys.add(key);
            this.volsnap.put(key, version);
        }else {
            this.bufferedVolsnap.put(key, version);
        }
    }

    public void setYieldCurveSnap(String key, int version,  boolean isAddressUsed){
        if(!isAddressUsed) {
            this.yieldCurveKeys.add(key);
            this.yieldCurveSnap.put(key, version);
        }else {
            this.bufferedYieldCurveSnap.put(key, version);
        }
    }

    public long getSnaptime() {
        return snaptime;
    }

    public Map<String, Integer> getSpotsnap() {
        return spotsnap.asMap();
    }

    public Map<String, Integer> getVolsnap() {
        return volsnap.asMap();
    }

    public Map<String, Integer> getYieldCurveSnap() {
        return yieldCurveSnap.asMap();
    }

    public Set<String> getSpotKeys() {
        return spotKeys;
    }

    public Set<String> getVolKeys() {
        return volKeys;
    }

    public Set<String> getYieldCurveKeys() {
        return yieldCurveKeys;
    }

    public void closeWindow(){
        clearCurrentKeys();
        transferBuffer();
        transferBufferKeys();
        clearBuffer();

    }

    private void clearBuffer() {
        this.bufferedSpotSnap.clear();
        this.bufferedVolsnap.clear();
        this.bufferedYieldCurveSnap.clear();
    }

    private void transferBufferKeys() {
        spotKeys.addAll(bufferedSpotSnap.keySet());
        volKeys.addAll(bufferedVolsnap.keySet());
        yieldCurveKeys.addAll(bufferedYieldCurveSnap.keySet());
    }

    private void transferBuffer() {
        spotsnap.putAll(bufferedSpotSnap);
        volsnap.putAll(bufferedVolsnap);
        yieldCurveSnap.putAll(bufferedYieldCurveSnap);
    }

    private void clearCurrentKeys() {
        this.spotKeys.clear();
        this.volKeys.clear();
        this.yieldCurveKeys.clear();
    }
}
