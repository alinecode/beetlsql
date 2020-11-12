package org.beetl.sql.sega.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.beetl.sql.sega.common.LocalSegaTransaction;
import org.beetl.sql.sega.common.SegaContext;
import org.beetl.sql.sega.common.SegaRollbackException;
import org.beetl.sql.sega.common.SegaTransaction;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaSegaContext extends SegaContext {
	KafkaSegaTransaction transaction = null;
	KafkaTemplate kafkaTemplate = null;
	String topic;
	KafkaSegaConfig config;
	public KafkaSegaContext(KafkaSegaConfig config){
		transaction = new KafkaSegaTransaction();
		this.config = config;
	}

	public KafkaSegaContext(KafkaSegaTransaction transaction,KafkaSegaConfig config){
		this.transaction = transaction;
		this.config = config;
	}
	@Override
	public void rollback() {
		boolean success = transaction.rollback();
		if(!success&&transaction.getTotalTry()<config.getMaxTry()){
			try {
				config.getTemplate().send(config.getRetrySegaTopic(),config.getObjectMapper().writeValueAsString(transaction));
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException("不能序列化 transaction "+e.getMessage());
			}
		}
	}

	@Override
	public SegaTransaction getTransaction() {
		return null;
	}
}
