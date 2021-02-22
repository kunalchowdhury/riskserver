package com.quantlogic.valuation.entity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class KryoSerializer<T> implements RedisSerializer<T> {
    private final Kryo kryo;

    public KryoSerializer(List<Class<?>> classes) {
        kryo = new Kryo();
        for (Class<?> clazz : classes) {
            kryo.register(clazz);
        }
    }
    @Override
    public byte[] serialize(T t) throws SerializationException {
        ByteArrayOutputStream objStream = new ByteArrayOutputStream();
        Output objOutput = new Output(objStream);
        kryo.writeClassAndObject(objOutput, t);
        objOutput.close();
        return objStream.toByteArray();
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return (T) kryo.readClassAndObject(new Input(bytes));
    }
}
