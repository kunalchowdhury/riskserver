package com.quantlogic.marketdatarepository;

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
        this.output = new Output(1024);

    }

    @Override
    public byte[] serialize(SpotPrice spotPrice) throws SerializationException {
        this.output.reset();
        spotPriceSerializer.write(this.kryo, this.output, spotPrice);
        return this.output.getBuffer();
    }

    @Override
    public SpotPrice deserialize(byte[] bytes) throws SerializationException {
        Input input = new Input(bytes, 0, bytes.length);
        return kryo.readObject(input, SpotPrice.class);
    }
}
