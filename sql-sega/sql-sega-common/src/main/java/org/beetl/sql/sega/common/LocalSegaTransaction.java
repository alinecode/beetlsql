package org.beetl.sql.sega.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class LocalSegaTransaction implements SegaTransaction {
	String id;
	List<SegaTaskTrace> tasks = new ArrayList<>();
	boolean success = true;
	int count = 0;

	public LocalSegaTransaction(){
		id = UUID.randomUUID().toString();
	}

	@Override
	public String getSegaTransactionId() {
		return id;
	}

	@Override
	public void addTask(SegaRollbackTask task){
		tasks.add(new SegaTaskTrace(task) );
	}
	@Override
	public boolean rollback(){
		List<SegaRollbackTask> failureTask = new ArrayList<>();
		for(SegaTaskTrace trace: tasks){
			trace.call();
			if(!trace.success){
				success = false;
			}
		}

		if(!success){
			//记录执行次数
			count++;
		}
		return success;
	}


	@Override
	public List<SegaRollbackTask> failureTaskAfterRollBack(){
		List<SegaRollbackTask> failure = tasks.stream().filter(segaTaskTrace -> !segaTaskTrace.isSuccess())
				.map(segaTaskTrace -> segaTaskTrace.rollbackTask).collect(Collectors.toList());
		return failure;
	}



	@Data
	static class SegaTaskTrace{
		SegaRollbackTask rollbackTask  = null;
		boolean success = false;
		public SegaTaskTrace(SegaRollbackTask rollbackTask){
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
