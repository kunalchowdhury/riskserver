package com.quantlogic.common.message;

public class MarkerAndAddressResevationMessage {
    private long snapshotTime;
    private String id;
    private int version;
    private boolean closeBucket;
    private boolean reserveAddress;
    private boolean freeAddress;
    private int addressLoc;

    public long getSnapshotTime() {
        return snapshotTime;
    }

    public void setSnapshotTime(long snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isCloseBucket() {
        return closeBucket;
    }

    public void setCloseBucket(boolean closeBucket) {
        this.closeBucket = closeBucket;
    }

    public boolean isReserveAddress() {
        return reserveAddress;
    }

    public void setReserveAddress(boolean reserveAddress) {
        this.reserveAddress = reserveAddress;
    }

    public boolean isFreeAddress() {
        return freeAddress;
    }

    public void setFreeAddress(boolean freeAddress) {
        this.freeAddress = freeAddress;
    }

    public int getAddressLoc() {
        return addressLoc;
    }

    public void setAddressLoc(int addressLoc) {
        this.addressLoc = addressLoc;
    }
}
