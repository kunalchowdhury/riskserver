package com.quantlogic.dto.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;

public class BlackVarianceVolSerializer  extends Serializer<TimedBlackVarianceVolatility> {
    @Override
    public void write(Kryo kryo, Output output, TimedBlackVarianceVolatility blackVarianceVolatility) {
        output.writeLong(blackVarianceVolatility.getValuationDate());
        output.writeByte(blackVarianceVolatility.getCalendar());
        long[] expirations = blackVarianceVolatility.getExpirations();
        output.write(expirations.length);
        output.writeLongs(expirations, 0, expirations.length);
        double[] strikes = blackVarianceVolatility.getStrikes();
        output.write(strikes.length);
        output.writeDoubles(strikes, 0, strikes.length);
        output.writeByte(blackVarianceVolatility.getCurDayCounter());
        double[][] vols = blackVarianceVolatility.getVols();
        output.write(vols[0].length);
        for (double[] vol : vols) {
            output.writeDoubles(vol, 0, vols[0].length);
        }
        output.writeLong(blackVarianceVolatility.getVersion());
        output.write(blackVarianceVolatility.getShardId());
        output.writeString(blackVarianceVolatility.getName());
    }

    @Override
    public TimedBlackVarianceVolatility read(Kryo kryo, Input input, Class<? extends TimedBlackVarianceVolatility> aClass) {
        long valuationDate = input.readLong();
        byte calendar = input.readByte();
        int expirationsLen = input.read();
        long[] expiration = input.readLongs(expirationsLen);
        int strikeslen = input.read();
        double[] strikes = input.readDoubles(strikeslen);
        byte curDayCounter = input.readByte();
        int volsLen = input.read();
        double[][] vols = new double[volsLen][volsLen];
        for (int i = 0; i < volsLen; i++) {
            vols[i] = input.readDoubles(volsLen);
        }
        long version = input.readLong();
        int shardId = input.read();
        String name = input.readString();
        return new TimedBlackVarianceVolatility(valuationDate, calendar,
                expiration, strikes, curDayCounter , vols, version, shardId, name);
    }
}
