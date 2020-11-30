package org.beetl.sql.saga.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class LocalSagaTransaction implements SagaTransaction {
	protected String id;
	protected List<SagaRollbackTask> tasks = new ArrayList<>();
	protected boolean success = true;

	public LocalSagaTransaction(){
		id = UUID.randomUUID().toString();
	}

	@Override
	public String transactionId() {
		return id;
	}

	@Override
	public void addTask(SagaRollbackTask task){
		tasks.add(task);
	}
	@Override
	public boolean rollback(){
		try{
			for(SagaRollbackTask task: tasks){
				success = task.call();
				if(!success){
					return false;
				}
			}
		}catch (Exception ex){
			return false;
		}


		return true;
	}



}
