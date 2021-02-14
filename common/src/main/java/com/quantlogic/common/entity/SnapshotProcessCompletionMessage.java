package com.quantlogic.common.entity;

public class SnapshotProcessCompletionMessage {
    private int startMemAddress;
    private boolean done;

    public int getStartMemAddress() {
        return startMemAddress;
    }

    public void setStartMemAddress(int startMemAddress) {
        this.startMemAddress = startMemAddress;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
