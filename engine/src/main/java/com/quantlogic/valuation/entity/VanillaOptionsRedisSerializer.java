package com.quantlogic.valuation.entity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.quantlogic.common.entity.TimedVanillaOption;
import com.quantlogic.entity.serializer.VanillaOptionSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class VanillaOptionsRedisSerializer implements RedisSerializer<TimedVanillaOption> {
    private final Kryo kryo;
    private final Output output;
    private final VanillaOptionSerializer vanillaOptionSerializer;

    public VanillaOptionsRedisSerializer() {
        this.vanillaOptionSerializer = new VanillaOptionSerializer();
        this.kryo = new Kryo();
        this.kryo.register(TimedVanillaOption.class);
        this.output = new Output(1024);
    }

    @Override
    public byte[] serialize(TimedVanillaOption vanillaOption) throws SerializationException {
        vanillaOptionSerializer.write(this.kryo, this.output, vanillaOption);
        byte[] buffer = this.output.getBuffer();
        this.output.flush();
        return buffer;
    }

    @Override
    public TimedVanillaOption deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null){
            return null;
        }
        Input input = new Input(bytes, 0, bytes.length);
        return vanillaOptionSerializer.read(kryo, input, TimedVanillaOption.class);
    }
}
