package com.quantlogic.entity.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.SpotPrice;

public class SpotPriceSerializer extends Serializer<SpotPrice> {

    @Override
    public void write(Kryo kryo, Output output, SpotPrice spotPrice) {
        output.writeString(spotPrice.getTicker().trim());
        output.writeDouble(spotPrice.getMid());
        output.writeDouble(spotPrice.getHi());
        output.writeDouble(spotPrice.getLo());
        output.writeDouble(spotPrice.getOpen());
        output.writeDouble(spotPrice.getClose());
        output.writeString(spotPrice.getName().trim());
        output.write(spotPrice.getVersion());
    }

    @Override
    public SpotPrice read(Kryo kryo, Input input, Class<? extends SpotPrice> aClass) {
        return new SpotPrice(input.readString().trim(), input.readDouble(), input.readDouble(),
                input.readDouble(), input.readDouble(), input.readDouble(), input.readString().trim(),
                input.read());
    }
}
