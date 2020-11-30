package org.beetl.sql.saga.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.beetl.sql.saga.common.SagaTransaction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 反序列化，采用二进制
 * @author xiandafu
 */
public class ByteDeserializer implements Deserializer {

	@Override
	public Object deserialize(String s, byte[] bytes) {
			ByteArrayInputStream ins = new ByteArrayInputStream(bytes);
			try {
				ObjectInputStream ois = new ObjectInputStream(ins);
				SagaTransaction sagaTransaction = (SagaTransaction)ois.readObject();
				return sagaTransaction;
			} catch (IOException | SecurityException | ClassNotFoundException e) {
				throw new RuntimeException("不能反序列化",e);
			}
	}

}
