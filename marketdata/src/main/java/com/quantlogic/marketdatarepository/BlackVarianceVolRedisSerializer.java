package com.quantlogic.marketdatarepository;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.TimedBlackVarianceVolatility;
import com.quantlogic.entity.serializer.BlackVarianceVolSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class BlackVarianceVolRedisSerializer implements RedisSerializer<TimedBlackVarianceVolatility> {
    private final Kryo kryo;
    private final Output output;
    private final BlackVarianceVolSerializer blackVarianceVolSerializer;

    public BlackVarianceVolRedisSerializer() {
        this.blackVarianceVolSerializer = new BlackVarianceVolSerializer();
        this.kryo = new Kryo();
        this.output = new Output(1024);
    }

    @Override
    public byte[] serialize(TimedBlackVarianceVolatility blackVarianceVolatility) throws SerializationException {
        this.output.reset();
        blackVarianceVolSerializer.write(this.kryo, this.output, blackVarianceVolatility);
        return this.output.getBuffer();
    }

    @Override
    public TimedBlackVarianceVolatility deserialize(byte[] bytes) throws SerializationException {
        Input input = new Input(bytes, 0, bytes.length);
        return kryo.readObject(input, TimedBlackVarianceVolatility.class);
    }
}
