package org.beetl.sql.saga.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.beetl.sql.saga.common.SagaTransaction;

import java.io.UnsupportedEncodingException;

public class JacksonSerializer implements Serializer<KafkaSagaTransaction> {
	public static final  ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public byte[] serialize(String topic, KafkaSagaTransaction data) {
		byte[] bs = new byte[0];
		try {
			String str  = objectMapper.writeValueAsString(data);
			bs = str.getBytes("UTF-8");
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return bs;
	}
}
