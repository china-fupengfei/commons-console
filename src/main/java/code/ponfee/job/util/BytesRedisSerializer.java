package code.ponfee.job.util;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Byte array redis serialize
 * 
 * @author Ponfee
 */
public class BytesRedisSerializer implements RedisSerializer<byte[]> {

    @Override
    public byte[] serialize(byte[] t) {
        return t;
    }

    @Override
    public byte[] deserialize(byte[] bytes) {
        return bytes;
    }

}
