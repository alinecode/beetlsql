package org.beetl.sql.saga.ms.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.beetl.sql.saga.ms.client.task.*;

/**
 * 与saga-server 交互的api
 */
public class SagaServerClient {
	SagaLevel3ClientConfig level3Config;

	public SagaServerClient(SagaLevel3ClientConfig level3Config) {
		this.level3Config = level3Config;
	}

	/**
	 * 标记开始
	 * @param gid
	 * @param time
	 */
	public void start(String gid, long time) {
		StartClientTask startTask = new StartClientTask(gid, time);
		startTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), toString(startTask));
	}


	/**
	 * 发送回滚任务到服务器
	 * @param tasks
	 */
	public void sendRollbackTask(String gid, long time, SagaLevel3Transaction tasks) {
		RollbackClientTask rollbackTask = new RollbackClientTask(gid, time, tasks);
		rollbackTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), toString(rollbackTask));
	}

	/**
	 * 发送回滚任务到服务器
	 * @param tasks
	 */
	public void sendRollbackTaskInCommit(String gid, long time, SagaLevel3Transaction tasks) {
		RollbackInCommitClientTask rollbackInCommitTask = new RollbackInCommitClientTask(gid, time, tasks);
		rollbackInCommitTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), toString(rollbackInCommitTask));
	}

	public void rollbackSuccess(String gid, long time) {
		RollbackSuccessClientTask rollbackSuccessTask = new RollbackSuccessClientTask(gid, time);
		rollbackSuccessTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), toString(rollbackSuccessTask));

	}

	public void rollbackFailure(String gid, long time, SagaLevel3Transaction tasks) {
		RollbackFailureClientTask rollbackFailureTask = new RollbackFailureClientTask(gid, time, tasks);
		rollbackFailureTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), toString(rollbackFailureTask));

	}

	protected  String toString(ClientTask task)  {
		try {
			return level3Config.getObjectMapper().writeValueAsString(task);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("不能序列化 "+task.getClass());
		}
	}


}
