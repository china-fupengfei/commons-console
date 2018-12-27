package code.ponfee.job.util;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Protostuff redis serialize utility
 * 
 * @author Ponfee
 */
public class ProtostuffRedisSerializer<T> implements RedisSerializer<T> {

    private final ProtoWrapper wrapper;
    private final Schema<ProtoWrapper> schema;
    private final LinkedBuffer buffer;

    public ProtostuffRedisSerializer() {
        this.wrapper = new ProtoWrapper();
        this.schema = RuntimeSchema.getSchema(ProtoWrapper.class);
        this.buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    }

    @Override
    public byte[] serialize(T obj) throws SerializationException {
        if (obj == null) {
            return null;
        }

        wrapper.data = obj;
        try {
            return ProtostuffIOUtil.toByteArray(wrapper, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) throws SerializationException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        ProtoWrapper wrapper = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, wrapper, schema);
        return (T) wrapper.data;
    }

    private static class ProtoWrapper implements Serializable {
        private static final long serialVersionUID = -7170699878063967720L;
        private Object data;
    }

}
