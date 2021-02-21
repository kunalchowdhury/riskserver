package com.quantlogic.valuation.entity;

public class ValuationResponse {
    private double npv;
    private double delta;
    private double gamma;
    private double theta;
    private double rho;

    public double getNpv() {
        return npv;
    }

    public void setNpv(double npv) {
        this.npv = npv;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getRho() {
        return rho;
    }

    public void setRho(double rho) {
        this.rho = rho;
    }

    @Override
    public String toString() {
        return "ValuationResponse{" +
                "npv=" + npv +
                ", delta=" + delta +
                ", gamma=" + gamma +
                ", theta=" + theta +
                ", rho=" + rho +
                '}';
    }
}
