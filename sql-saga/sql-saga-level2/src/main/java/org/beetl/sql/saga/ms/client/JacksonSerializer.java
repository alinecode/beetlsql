package org.beetl.sql.saga.ms.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.io.UnsupportedEncodingException;

public class JacksonSerializer implements Serializer<KafkaLevel2Transaction> {
	public static final  ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public byte[] serialize(String topic, KafkaLevel2Transaction data) {
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
