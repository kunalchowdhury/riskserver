package com.quantlogic.common.entity;

public class SnapshotAllocationMessage {
    private long startMemAddress;
    private boolean done;

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
}
