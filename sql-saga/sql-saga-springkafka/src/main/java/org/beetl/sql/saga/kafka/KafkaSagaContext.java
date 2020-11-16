package org.beetl.sql.saga.kafka;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.beetl.sql.saga.common.*;
import org.beetl.sql.saga.common.ami.SagaDeleteByIdAMI;
import org.beetl.sql.saga.common.ami.SagaInsertAMI;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.Callable;

/**
 * 回滚事务，如果没有完全成功，则发送到kafka队列，在尝试多次后，仍然没有成功，发送给
 *
 * @see  KafkaSagaConfig
 * @author xiandafu
 */
public class KafkaSagaContext extends SagaContext {
	KafkaSagaTransaction transaction = null;
	KafkaTemplate kafkaTemplate = null;
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
			if(transaction.getTotalTry()<=config.getMaxTry()){
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

	/**
	 * 服务调用也放入context管理
	 * @param callable
	 * @param runnable ，必须保证可被json序列化和反序列化，比如，提供一个空的构造函数
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	@Override
	public <T> T callService(Callable<T> callable, Runnable runnable) throws Exception{
		try{
			return callable.call();
		}catch(Exception ex){
			this.getTransaction().addTask(new LocalSagaContext.FunctionCallback(runnable));
			throw ex;
		}
	}

	@Data
	public static class FunctionCallback implements SagaRollbackTask {
		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
		Runnable function;
		public FunctionCallback(Runnable function){
			this.function = function;
		}
		@Override
		public boolean call() {
			try{
				function.run();
				return true;
			}catch (Exception ex){
				return false;
			}
		}
	}

	public static void main(String[] args) throws Exception{
		KafkaSagaTransaction transaction = new KafkaSagaTransaction();
		transaction.addTask(new SagaDeleteByIdAMI.DeleteSagaRollbackTask("nac",32));
		transaction.addTask(new SagaInsertAMI.InsertSagaRollbackTask("abc",KafkaSagaContext.class,1212));
		ObjectMapper mapper = new ObjectMapper();
		String str =  mapper.writeValueAsString(transaction);
		System.out.println(str);

		KafkaSagaTransaction transaction1 = mapper.readValue(str,KafkaSagaTransaction.class);
		return;
	}

}
