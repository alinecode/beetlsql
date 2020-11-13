package org.beetl.sql.saga.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaTransaction;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 回滚事务，如果没有完全成功，则发送到kafka队列，在尝试多次后，仍然没有成功，发送给
 *
 * @see  KafkaSagaConfig
 * @author xiandafu
 */
public class KafkaSagaContext extends SagaContext {
	KafkaSagaTransaction transaction = null;
	KafkaTemplate kafkaTemplate = null;
	String topic;
	KafkaSagaConfig config;
	public KafkaSagaContext(KafkaSagaConfig config){
		transaction = new KafkaSagaTransaction();
		this.config = config;
	}

	public KafkaSagaContext(KafkaSagaTransaction transaction, KafkaSagaConfig config){
		this.transaction = transaction;
		this.config = config;
	}
	@Override
	public void rollback() {
		boolean success = transaction.rollback();
		if(success){
			return ;
		}
		if(transaction.getTotalTry()<config.getMaxTry()){
			try {
				config.getTemplate().send(config.getRetrySegaTopic(),config.getObjectMapper().writeValueAsString(transaction));
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException("不能序列化 transaction "+e.getMessage());
			}
		}else{
			//丢入
			try {
				config.getTemplate().send(config.getFailSegaTopic(),config.getObjectMapper().writeValueAsString(transaction));
			} catch (JsonProcessingException e) {
				//不可能发生
				throw new IllegalArgumentException("不能序列化 transaction "+e.getMessage());
			}
		}
	}

	@Override
	public SagaTransaction getTransaction() {
		return null;
	}
}
