package com.quantlogic.valuation.entity;

public class ErroredValuationResponse extends ValuationResponse{

    private ErroredValuationResponse() {}

    public static final ErroredValuationResponse INSTANCE = new ErroredValuationResponse();

    @Override
    public double getNpv() {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public void setNpv(double npv) {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public double getDelta() {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public void setDelta(double delta) {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public double getGamma() {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public void setGamma(double gamma) {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public double getTheta() {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public void setTheta(double theta) {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public double getRho() {
        throw new IllegalStateException("Exception occured in valuation");
    }

    @Override
    public void setRho(double rho) {
        throw new IllegalStateException("Exception occured in valuation");
    }
}
