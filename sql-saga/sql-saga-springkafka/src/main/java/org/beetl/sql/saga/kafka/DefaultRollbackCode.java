package org.beetl.sql.saga.kafka;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.beetl.sql.saga.common.SagaTransaction;

import java.io.*;

public class DefaultRollbackCode implements RollbackCoder {

	@Override
	public Object encode(SagaTransaction obj) {
		ByteArrayOutputStream ba=new ByteArrayOutputStream();
		ObjectOutputStream oos= null;
		try {
			oos = new ObjectOutputStream(ba);
			oos.writeObject(obj);
			byte[] bytes=ba.toByteArray();
			return bytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public SagaTransaction decode(Object obj) {
		if(!(obj instanceof  byte[])){
			throw new IllegalArgumentException(" 期望 是字节数组");
		}
		byte[] bs = (byte[])obj;
		ByteArrayInputStream ins = new ByteArrayInputStream(bs);
		try {
			ObjectInputStream ois = new ObjectInputStream(ins);
			SagaTransaction sagaTransaction = (SagaTransaction)ois.readObject();
			return sagaTransaction;
		} catch (IOException | SecurityException | ClassNotFoundException e) {
			throw new RuntimeException("不能反序列化",e);
		}
	}
}
