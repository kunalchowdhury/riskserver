package com.quantlogic.valuation.entity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.SpotPrice;
import com.quantlogic.entity.serializer.SpotPriceSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class SpotPriceRedisSerializer implements RedisSerializer<SpotPrice> {

    private final Kryo kryo;
    private final Output output;
    private final SpotPriceSerializer spotPriceSerializer;

    public SpotPriceRedisSerializer() {
        this.spotPriceSerializer = new SpotPriceSerializer();
        this.kryo = new Kryo();
        this.kryo.register(SpotPrice.class);
        this.output = new Output(1024);

    }

    @Override
    public byte[] serialize(SpotPrice spotPrice) throws SerializationException {
        spotPriceSerializer.write(this.kryo, this.output, spotPrice);
        byte[] buffer = this.output.getBuffer();
        this.output.flush();
        return buffer;
    }

    @Override
    public SpotPrice deserialize(byte[] bytes) throws SerializationException {
         if(bytes == null){
             return null;
         }
         Input input = new Input(bytes, 0, bytes.length);
         return spotPriceSerializer.read(kryo, input, SpotPrice.class);
    }
}
