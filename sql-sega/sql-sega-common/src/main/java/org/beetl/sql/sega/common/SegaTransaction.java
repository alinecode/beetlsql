package org.beetl.sql.sega.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SegaTransaction {
	List<SegaTaskTrace> tasks = new ArrayList<>();
	boolean success = true;
	int count = 0;
	public void addTask(SegaRollbackTask task){
		tasks.add(new SegaTaskTrace(task) );
	}
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

	public void failureTask(List<SegaRollbackTask> failureTask,SegaRollbackTask task){
		success = false;
		failureTask.add(task);
	}

	public void transactionFailure(){

	}
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
