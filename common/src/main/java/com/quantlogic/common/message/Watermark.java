package com.quantlogic.common.message;

public class Watermark extends MarkerAndAddressReservationMessage {

    public static final Watermark INSTANCE = new Watermark();
    private Watermark() {}

    @Override
    public String toString() {
        return "Watermark{}";
    }
}
