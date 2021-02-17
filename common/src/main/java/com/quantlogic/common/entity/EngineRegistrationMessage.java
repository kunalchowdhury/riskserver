package com.quantlogic.common.entity;

public class EngineRegistrationMessage {
    private String pid;
    private String hostId;
    private String spotids;    // command separated spot ids
    private String volIds;      // comma separated vol ids
    private String yieldCurveIds;   // command separated yieldCurve Ids

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getSpotids() {
        return spotids;
    }

    public void setSpotids(String spotids) {
        this.spotids = spotids;
    }

    public String getVolIds() {
        return volIds;
    }

    public void setVolIds(String volIds) {
        this.volIds = volIds;
    }

    public String getYieldCurveIds() {
        return yieldCurveIds;
    }

    public void setYieldCurveIds(String yieldCurveIds) {
        this.yieldCurveIds = yieldCurveIds;
    }

    @Override
    public String toString() {
        return "EngineRegistrationMessage{" +
                "pid='" + pid + '\'' +
                ", hostId='" + hostId + '\'' +
                ", spotids='" + spotids + '\'' +
                ", volIds='" + volIds + '\'' +
                ", yieldCurveIds='" + yieldCurveIds + '\'' +
                '}';
    }
}
