package org.beetl.sql.saga.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.beetl.sql.saga.common.LocalSagaTransaction;
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
		newTransaction();
		this.config = config;
	}

	public KafkaSagaContext(KafkaSagaTransaction transaction, KafkaSagaConfig config){
		this.transaction = transaction;
		this.config = config;
	}
	@Override
	public void rollback() {
		try{
			boolean success = transaction.rollback();
			if(success){
				return ;
			}
			if(transaction.getTotalTry()<config.getMaxTry()){
				config.getTemplate().send(config.getRetrySegaTopic(), transaction);
			}else{
				//丢入失败队列
				config.getTemplate().send(config.getFailSegaTopic(), transaction);
			}
		}finally {
			newTransaction();
		}

	}

	@Override
	public SagaTransaction getTransaction() {
		return transaction;
	}
	protected  void newTransaction(){
		transaction = new KafkaSagaTransaction();
	}

}
