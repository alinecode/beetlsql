package org.beetl.sql.saga.ms.server.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.common.annotation.Rollback;
import org.beetl.sql.saga.ms.client.task.*;
import org.beetl.sql.saga.ms.server.service.SagaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

@Configuration
public class KafkaConfig {
	@Autowired
	SagaManager sagaManager;
	@KafkaListener(topics = "#{'${beetlsql-saga.kafka.server-topic}'}")
	public void aiEvent(List<ConsumerRecord<?, Task>> records) throws Exception {
		//TODO,按照gid分堆，多线程并行处理
		for(ConsumerRecord<?, Task> record:records){
			Task task = record.value();
			if(task instanceof StartTask){
				StartTask startTask = (StartTask)task;
				sagaManager.addStartTask(startTask.getGid(),startTask.getTime(),startTask.getAppName());
			}else if(task instanceof RollbackInCommitTask){
				RollbackInCommitTask rollbackInCommitTask =(RollbackInCommitTask)task;
				sagaManager.addRollbackBySuccessCommit(rollbackInCommitTask.getGid(),rollbackInCommitTask.getTime()
						,rollbackInCommitTask.getAppName(),rollbackInCommitTask.getRollback());
			}else if(task instanceof RollbackTask){
				RollbackTask rollbackTask = (RollbackTask)task;
				sagaManager.addRollbackAfterException(rollbackTask.getGid(),rollbackTask.getTime()
						,rollbackTask.getAppName(),rollbackTask.getRollback());;
			}else if(task instanceof RollbackFailureTask){
				RollbackFailureTask rollbackFailureTask =(RollbackFailureTask)task;
				sagaManager.notifyRollback(rollbackFailureTask.getGid(),rollbackFailureTask.getTime(),false
						,rollbackFailureTask.getAppName());
			}else if(task instanceof RollbackSuccessTask){
				RollbackSuccessTask rollbackSuccessTask =(RollbackSuccessTask)task;
				sagaManager.notifyRollback(rollbackSuccessTask.getGid(),rollbackSuccessTask.getTime(),true
						,rollbackSuccessTask.getAppName());
			}else{
				throw new IllegalStateException("error task "+task.getClass());
			}
		}
	}

}
