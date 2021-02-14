package com.quantlogic.dto.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.dto.SpotPriceDTO;

public class SpotPriceDTOSerializer extends Serializer<SpotPriceDTO> {

    @Override
    public void write(Kryo kryo, Output output, SpotPriceDTO spotPrice) {
        output.writeString(spotPrice.getTicker().trim());
        output.writeDouble(spotPrice.getMid());
        output.writeDouble(spotPrice.getHi());
        output.writeDouble(spotPrice.getLo());
        output.writeDouble(spotPrice.getOpen());
        output.writeDouble(spotPrice.getClose());
        output.write(spotPrice.getVersion());
    }

    @Override
    public SpotPriceDTO read(Kryo kryo, Input input, Class<? extends SpotPriceDTO> aClass) {
        return new SpotPriceDTO(input.readString().trim(), input.readDouble(), input.readDouble(),
                input.readDouble(), input.readDouble(), input.readDouble(), input.read());
    }
}
