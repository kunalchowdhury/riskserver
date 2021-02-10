package com.quantlogic.dto.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.SpotPrice;

public class SpotPriceSerializer extends Serializer<SpotPrice> {

    @Override
    public void write(Kryo kryo, Output output, SpotPrice spotPrice) {
        output.writeString(spotPrice.getTicker());
        output.writeDouble(spotPrice.getMid());
        output.writeDouble(spotPrice.getHi());
        output.writeDouble(spotPrice.getLo());
        output.writeDouble(spotPrice.getOpen());
        output.writeDouble(spotPrice.getClose());
        output.writeLong(spotPrice.getSnapshot());
        output.writeString(spotPrice.getName());
        output.writeLong(spotPrice.getVersion());
    }

    @Override
    public SpotPrice read(Kryo kryo, Input input, Class<? extends SpotPrice> aClass) {
        return new SpotPrice(input.readString(), input.readDouble(), input.readDouble(),
                input.readDouble(), input.readDouble(), input.readDouble(), input.readLong(), input.readString(),
                input.readLong());
    }
}
