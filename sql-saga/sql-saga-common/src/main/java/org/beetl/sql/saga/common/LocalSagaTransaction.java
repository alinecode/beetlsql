package org.beetl.sql.saga.common;

import lombok.Data;

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
	public String getSegaTransactionId() {
		return id;
	}

	@Override
	public void addTask(SagaRollbackTask task){
		tasks.add(new SegaTaskTrace(task) );
	}
	@Override
	public boolean rollback(){
		List<SagaRollbackTask> failureTask = new ArrayList<>();
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


	@Override
	public List<SagaRollbackTask> failureTaskAfterRollBack(){
		List<SagaRollbackTask> failure = tasks.stream().filter(segaTaskTrace -> !segaTaskTrace.isSuccess())
				.map(segaTaskTrace -> segaTaskTrace.rollbackTask).collect(Collectors.toList());
		return failure;
	}



	@Data
	static class SegaTaskTrace{
		SagaRollbackTask rollbackTask  = null;
		boolean success = false;
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
			}catch(Exception ex){
				success = false;
			}
		}
	}

}
