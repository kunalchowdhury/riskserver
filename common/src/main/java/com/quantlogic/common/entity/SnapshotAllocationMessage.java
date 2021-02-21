package com.quantlogic.common.entity;

import java.util.ArrayList;
import java.util.List;

public class SnapshotAllocationMessage {
    private String correlationId;
    private long startMemAddress;
    private boolean done;
    private String spotIds;
    private String volIds;
    private String yieldCurveIds;
    private boolean update;
    private List<Integer> addressUpdates;
    private String mappedFile;
    private long sz;
    private String cacheId;

    public SnapshotAllocationMessage() {
        this.addressUpdates = new ArrayList<>();
    }

    public SnapshotAllocationMessage(SnapshotAllocationMessage snapshotAllocationMessage) {
        this.addressUpdates = new ArrayList<>();
        this.addressUpdates.addAll(snapshotAllocationMessage.addressUpdates);
        this.correlationId = snapshotAllocationMessage.correlationId;
        this.startMemAddress = snapshotAllocationMessage.startMemAddress;
        this.done = snapshotAllocationMessage.done;
        this.spotIds = snapshotAllocationMessage.spotIds;
        this.volIds = snapshotAllocationMessage.volIds;
        this.yieldCurveIds = snapshotAllocationMessage.yieldCurveIds;
        this.update = snapshotAllocationMessage.update;
        this.mappedFile = snapshotAllocationMessage.mappedFile;
        this.sz = snapshotAllocationMessage.sz;
        this.cacheId = snapshotAllocationMessage.cacheId;

    }

    public long getStartMemAddress() {
        return startMemAddress;
    }

    public void setStartMemAddress(long startMemAddress) {
        this.startMemAddress = startMemAddress;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setSpotIds(String spotIds) {
        this.spotIds = spotIds;
    }

    public String getSpotIds() {
        return spotIds;
    }

    public void setVolIds(String volIds) {
        this.volIds = volIds;
    }

    public String getVolIds() {
        return volIds;
    }

    public void setYieldCurveIds(String yieldCurveIds) {
        this.yieldCurveIds = yieldCurveIds;
    }

    public String getYieldCurveIds() {
        return yieldCurveIds;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public List<Integer> getAddressUpdates() {
        return addressUpdates;
    }

    public void setAddressUpdates(List<Integer> addressUpdates) {
        this.addressUpdates = addressUpdates;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getMappedFile() {
        return mappedFile;
    }

    public void setMappedFile(String mappedFile) {
        this.mappedFile = mappedFile;
    }

    public long getSz() {
        return sz;
    }

    public void setSz(long sz) {
        this.sz = sz;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    @Override
    public String toString() {
        return "SnapshotAllocationMessage{" +
                "correlationId='" + correlationId + '\'' +
                ", startMemAddress=" + startMemAddress +
                ", done=" + done +
                ", spotIds='" + spotIds + '\'' +
                ", volIds='" + volIds + '\'' +
                ", yieldCurveIds='" + yieldCurveIds + '\'' +
                ", update=" + update +
                ", addressUpdates=" + addressUpdates +
                ", mappedFile='" + mappedFile + '\'' +
                ", sz=" + sz +
                ", cacheId='" + cacheId + '\'' +
                '}';
    }

    public void setSize(long sz) {
        this.sz = sz ;
    }
}
