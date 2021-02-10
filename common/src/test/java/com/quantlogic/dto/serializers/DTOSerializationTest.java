package com.quantlogic.dto.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import com.quantlogic.common.entity.TimedVanillaOption;
import org.junit.Assert;
import org.junit.Test;

public class DTOSerializationTest {

    @Test
    public void serializeSpotPrice(){
        SpotPrice spotPriceDTO = new SpotPrice("TEST", 12, 14, 16, 18 ,20, System.currentTimeMillis(), "TEST", 1);
        Kryo kryo = new Kryo();
        kryo.register(SpotPrice.class, new SpotPriceSerializer());

        Output output = new Output(1024);
        kryo.writeObject(output, spotPriceDTO);

        Input input = new Input(output.getBuffer(), 0, output.position());
        SpotPrice object2 = kryo.readObject(input, SpotPrice.class);

        Assert.assertEquals(spotPriceDTO, object2);
    }

    @Test
    public void serializeBlackVolVariance(){
        long l = System.currentTimeMillis();
        long[] expiratons = new long[]{System.currentTimeMillis(), System.currentTimeMillis()};
        double[] strikes = new double[]{20.0, 21.0};
        double[][] vols = new double[][]{{0.4, 0.5}, {0.24, 0.53}};
        TimedBlackVarianceVolatility dto = new TimedBlackVarianceVolatility(l, (byte)1, expiratons,  strikes, (byte)1, vols, 1, 1, "TESTVOL" );

        Kryo kryo = new Kryo();
        kryo.register(TimedBlackVarianceVolatility.class, new BlackVarianceVolSerializer());

        Output output = new Output(1024);
        kryo.writeObject(output, dto);


        Input input = new Input(output.getBuffer(), 0, output.position());
        TimedBlackVarianceVolatility object2 = kryo.readObject(input, TimedBlackVarianceVolatility.class);

        Assert.assertEquals(dto, object2);
    }

    @Test
    public void serializeVanillaOption(){

        TimedVanillaOption option = new TimedVanillaOption(12.0, "UND1", 10.0, 19.0, 34.5,
                System.currentTimeMillis(), System.currentTimeMillis(), (byte)1,  (byte)1,  (byte)1, "TICK1", 1, 1,"SYM1" );
        Kryo kryo = new Kryo();
        kryo.register(TimedVanillaOption.class, new VanillaOptionSerializer());

        Output output = new Output(1024);
        kryo.writeObject(output, option);

        Input input = new Input(output.getBuffer(), 0, output.position());
        TimedVanillaOption object2 = kryo.readObject(input, TimedVanillaOption.class);

        Assert.assertEquals(option, object2);
    }
}
