package org.beetl.sql.saga.ms.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.common.LocalSagaContext;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaRollbackTask;
import org.beetl.sql.saga.common.SagaTransaction;
import org.beetl.sql.saga.common.ami.SagaDeleteByIdAMI;
import org.beetl.sql.saga.common.ami.SagaInsertAMI;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.Callable;

/**
 * 回滚事务，如果没有完全成功，则发送到kafka队列，在尝试多次后，仍然没有成功，发送给
 *
 * @see  KafkaLevel2Config
 * @author xiandafu
 */
public class KafkaLevel2Context extends SagaContext {
	KafkaLevel2Transaction transaction = null;
	KafkaTemplate kafkaTemplate = null;
	KafkaLevel2Config config;
	public KafkaLevel2Context(KafkaLevel2Config config){
		newTransaction();
		this.config = config;
	}

	public KafkaLevel2Context(KafkaLevel2Transaction transaction, KafkaLevel2Config config){
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
		transaction = new KafkaLevel2Transaction();
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
	@Slf4j
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
				log.info(ex.getMessage(),ex);
				return false;
			}
		}
	}

	public static void main(String[] args) throws Exception{
		KafkaLevel2Transaction transaction = new KafkaLevel2Transaction();
		transaction.addTask(new SagaDeleteByIdAMI.DeleteSagaRollbackTask("nac",32));
		transaction.addTask(new SagaInsertAMI.InsertSagaRollbackTask("abc", KafkaLevel2Context.class,1212));
		ObjectMapper mapper = new ObjectMapper();
		String str =  mapper.writeValueAsString(transaction);
		System.out.println(str);

		KafkaLevel2Transaction transaction1 = mapper.readValue(str, KafkaLevel2Transaction.class);
		return;
	}

}
