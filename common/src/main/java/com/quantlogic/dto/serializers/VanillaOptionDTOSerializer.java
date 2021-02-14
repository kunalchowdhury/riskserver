package com.quantlogic.dto.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.dto.VanillaOptionDTO;

public class VanillaOptionDTOSerializer extends Serializer<VanillaOptionDTO> {
    @Override
    public void write(Kryo kryo, Output output, VanillaOptionDTO vanillaOption) {
        output.writeDouble(vanillaOption.getStrike());
        output.writeString(vanillaOption.getUnderlying().trim());
        output.writeDouble(vanillaOption.getRiskFreeRate());
        output.writeDouble(vanillaOption.getDividendYield());
        output.writeDouble(vanillaOption.getVolatility());
        output.writeLong(vanillaOption.getSettlementDate());
        output.writeLong(vanillaOption.getMaturity());
        output.writeByte(vanillaOption.getDayCount());
        output.writeByte(vanillaOption.getOptionType());
        output.writeByte(vanillaOption.getExcerciseType());
        output.writeString(vanillaOption.getTickerSymbol());
        output.write(vanillaOption.getShardId());
        output.write(vanillaOption.getVersion());
        output.writeString(vanillaOption.getName().trim());

    }

    @Override
    public VanillaOptionDTO read(Kryo kryo, Input input, Class<? extends VanillaOptionDTO> aClass) {
        return new VanillaOptionDTO(input.readDouble(), input.readString().trim(), input.readDouble(), input.readDouble(),
                input.readDouble(), input.readLong(), input.readLong(), input.readByte(), input.readByte(), input.readByte(),
                input.readString().trim(), input.read(), input.read(), input.readString().trim());
    }
}
