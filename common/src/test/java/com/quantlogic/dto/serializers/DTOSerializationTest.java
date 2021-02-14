package com.quantlogic.dto.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.dto.BlackVarianceVolatilityDTO;
import com.quantlogic.dto.SpotPriceDTO;
import com.quantlogic.dto.VanillaOptionDTO;
import org.junit.Assert;
import org.junit.Test;

public class DTOSerializationTest {

    @Test
    public void serializeSpotPrice(){
        SpotPriceDTO spotPriceDTO = new SpotPriceDTO("TEST", 12, 14, 16, 18 ,20, 1);
        Kryo kryo = new Kryo();
        kryo.register(SpotPriceDTO.class, new SpotPriceDTOSerializer());

        Output output = new Output(1024);
        kryo.writeObject(output, spotPriceDTO);

        Input input = new Input(output.getBuffer(), 0, output.position());
        SpotPriceDTO object2 = kryo.readObject(input, SpotPriceDTO.class);

        Assert.assertEquals(spotPriceDTO, object2);
    }

    @Test
    public void serializeBlackVolVariance(){
        long l = System.currentTimeMillis();
        long[] expiratons = new long[]{System.currentTimeMillis(), System.currentTimeMillis()};
        double[] strikes = new double[]{20.0, 21.0};
        double[][] vols = new double[][]{{0.4, 0.5}, {0.24, 0.53}};
        BlackVarianceVolatilityDTO dto = new BlackVarianceVolatilityDTO(l, (byte)1, expiratons,  strikes, (byte)1, vols, 1, 1, "TESTVOL" );

        Kryo kryo = new Kryo();
        kryo.register(BlackVarianceVolatilityDTO.class, new BlackVarianceVolDTOSerializer());

        Output output = new Output(1024);
        kryo.writeObject(output, dto);


        Input input = new Input(output.getBuffer(), 0, output.position());
        BlackVarianceVolatilityDTO object2 = kryo.readObject(input, BlackVarianceVolatilityDTO.class);

        Assert.assertEquals(dto, object2);
    }

    @Test
    public void serializeVanillaOption(){

        long settlementDate = System.currentTimeMillis();
        VanillaOptionDTO option = new VanillaOptionDTO(12.0, "UND1", 10.0, 19.0, 34.5,
                settlementDate, settlementDate, (byte)1,  (byte)1,  (byte)1, "TICK1", 1 , 1 , "OTC1");
        Kryo kryo = new Kryo();
        kryo.register(VanillaOptionDTO.class, new VanillaOptionDTOSerializer());

        Output output = new Output(1024);
        kryo.writeObject(output, option);

        Input input = new Input(output.getBuffer(), 0, output.position());
        VanillaOptionDTO object2 = kryo.readObject(input, VanillaOptionDTO.class);

        Assert.assertEquals(option, object2);
    }
}
