package com.quantlogic.entity.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.TimedVanillaOption;

public class VanillaOptionSerializer extends Serializer<TimedVanillaOption> {
    @Override
    public void write(Kryo kryo, Output output, TimedVanillaOption vanillaOption) {
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
        output.writeString(vanillaOption.getTickerSymbol().trim());
        output.write(vanillaOption.getVersion());
        output.write(vanillaOption.getShardId());
        output.writeString(vanillaOption.getName().trim());

    }

    @Override
    public TimedVanillaOption read(Kryo kryo, Input input, Class<? extends TimedVanillaOption> aClass) {
        return new TimedVanillaOption(input.readDouble(), input.readString().trim(), input.readDouble(), input.readDouble(),
                input.readDouble(), input.readLong(), input.readLong(), input.readByte(), input.readByte(), input.readByte(),
                input.readString().trim(), input.read(), input.read(), input.readString().trim());
    }
}
