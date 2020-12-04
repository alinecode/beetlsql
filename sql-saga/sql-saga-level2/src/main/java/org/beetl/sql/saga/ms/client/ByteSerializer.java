package org.beetl.sql.saga.ms.client;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.beetl.sql.saga.common.SagaTransaction;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 序列化到kafka，二进制
 * @author xiandafu
 */
public class ByteSerializer implements Serializer<SagaTransaction> {

	@Override
	public byte[] serialize(String s, SagaTransaction sagaTransaction) {
		ByteArrayOutputStream ba=new ByteArrayOutputStream();
		ObjectOutputStream oos= null;
		try {
			oos = new ObjectOutputStream(ba);
			oos.writeObject(sagaTransaction);
			byte[] bytes=ba.toByteArray();
			return bytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}




}
