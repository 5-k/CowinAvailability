package com.prateek.cowinAvailibility.dto.cowinResponse.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CowinResponseSerializer implements RedisSerializer {

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        // TODO Auto-generated method stub
        return null;
    }

}
