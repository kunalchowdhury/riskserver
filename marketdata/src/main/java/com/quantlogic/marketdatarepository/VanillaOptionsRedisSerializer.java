package com.quantlogic.marketdatarepository;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.TimedVanillaOption;
import com.quantlogic.dto.serializers.VanillaOptionSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class VanillaOptionsRedisSerializer implements RedisSerializer<TimedVanillaOption> {
    private final Kryo kryo;
    private final Output output;
    private final VanillaOptionSerializer vanillaOptionSerializer;

    public VanillaOptionsRedisSerializer() {
        this.vanillaOptionSerializer = new VanillaOptionSerializer();
        this.kryo = new Kryo();
        this.output = new Output(1024);
    }

    @Override
    public byte[] serialize(TimedVanillaOption vanillaOption) throws SerializationException {
        this.output.reset();
        vanillaOptionSerializer.write(this.kryo, this.output, vanillaOption);
        return this.output.getBuffer();
    }

    @Override
    public TimedVanillaOption deserialize(byte[] bytes) throws SerializationException {
        Input input = new Input(bytes, 0, bytes.length);
        return kryo.readObject(input, TimedVanillaOption.class);
    }
}
