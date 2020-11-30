package org.beetl.sql.saga.kafka;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;


public class JacksonDeserializer implements Deserializer<KafkaLevel2Transaction> {
	@Override
	public KafkaLevel2Transaction deserialize(String topic, byte[] data) {
		try {
			String str = new String(data,"UTF-8");
			return JacksonSerializer.objectMapper.readValue(str, KafkaLevel2Transaction.class);
		} catch (IOException e) {
			throw new RuntimeException("不能反序列化",e);
		}
	}
}
