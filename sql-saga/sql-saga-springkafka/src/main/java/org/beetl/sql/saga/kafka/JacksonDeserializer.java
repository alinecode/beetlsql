package org.beetl.sql.saga.kafka;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;


public class JacksonDeserializer implements Deserializer<KafkaSagaTransaction> {
	@Override
	public KafkaSagaTransaction deserialize(String topic, byte[] data) {
		try {
			String str = new String(data,"UTF-8");
			return JacksonSerializer.objectMapper.readValue(str,KafkaSagaTransaction.class);
		} catch (IOException e) {
			throw new RuntimeException("不能反序列化",e);
		}
	}
}
