package org.beetl.sql.saga.ms.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.common.SagaRollbackTask;
import org.beetl.sql.saga.common.SagaTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 提供一个jackson序列化
 */
@Data
public class SagaLevel3Transaction implements SagaTransaction {
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
	protected List<KafkaSagaTaskTrace> tasks = new ArrayList<>();
	protected String id;
	protected boolean success = true;
	public SagaLevel3Transaction(){
		id = UUID.randomUUID().toString();
	}

	@Override
	public String transactionId() {
		return id;
	}

	@Override
	public void addTask(SagaRollbackTask task){
		tasks.add(new KafkaSagaTaskTrace(task) );
	}
	@Override
	public boolean rollback(){
		for(KafkaSagaTaskTrace trace: tasks){
			trace.call();
			if(!trace.success){
				success = false;
			}
		}
		return success;
	}


	@Data
	@Slf4j
	public static class KafkaSagaTaskTrace implements java.io.Serializable {
		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
		protected  SagaRollbackTask rollbackTask  = null;
		protected boolean success = false;
		public KafkaSagaTaskTrace(){
			//序列化用
		}
		public KafkaSagaTaskTrace(SagaRollbackTask rollbackTask){
			this.rollbackTask = rollbackTask;
		}
		public void call(){
			try{
				if(success){
					//已经执行过了
					return ;
				}
				success = rollbackTask.call();
				log.info("execute rollback task "+rollbackTask.getClass()+":"+ rollbackTask+" success");
			}catch(Exception ex){
				log.info("execute rollback task "+rollbackTask.getClass()+":"+ rollbackTask+" failure "+ex.getMessage());
				success = false;
			}
		}

	}

}
