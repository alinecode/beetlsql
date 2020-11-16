package org.beetl.sql.saga.common;

import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class LocalSagaTransaction implements SagaTransaction {
	protected String id;
	protected List<SegaTaskTrace> tasks = new ArrayList<>();
	protected boolean success = true;
	protected  int totalTry = 0;

	public LocalSagaTransaction(){
		id = UUID.randomUUID().toString();
	}

	@Override
	public String transactionId() {
		return id;
	}

	@Override
	public void addTask(SagaRollbackTask task){
		tasks.add(new SegaTaskTrace(task) );
	}
	@Override
	public boolean rollback(){
		for(SegaTaskTrace trace: tasks){
			trace.call();
			if(!trace.success){
				success = false;
			}
		}
		if(!success){
			//记录执行次数
			totalTry++;
		}
		return success;
	}


	@Data
	@Slf4j
	public static class SegaTaskTrace implements java.io.Serializable{
		protected  SagaRollbackTask rollbackTask  = null;
		protected boolean success = false;
		public SegaTaskTrace(){
			//序列化用
		}
		public SegaTaskTrace(SagaRollbackTask rollbackTask){
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
