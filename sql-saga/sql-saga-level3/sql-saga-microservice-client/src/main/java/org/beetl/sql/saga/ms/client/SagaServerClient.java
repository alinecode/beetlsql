package org.beetl.sql.saga.ms.client;

import org.beetl.sql.saga.common.SagaTransaction;
import org.beetl.sql.saga.ms.client.task.*;

/**
 * 与saga-server 交互的api
 */
public class SagaServerClient {
	SagaLevel3Config level3Config;

	public SagaServerClient(SagaLevel3Config level3Config) {
		this.level3Config = level3Config;
	}

	/**
	 * 标记开始
	 * @param gid
	 * @param time
	 */
	public void start(String gid, long time) {
		StartTask startTask = new StartTask(gid, time);
		startTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), startTask);
	}


	/**
	 * 发送回滚任务到服务器
	 * @param tasks
	 */
	public void sendRollbackTask(String gid, long time, SagaTransaction tasks) {
		RollbackTask rollbackTask = new RollbackTask(gid, time, tasks);
		rollbackTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), rollbackTask);
	}

	/**
	 * 发送回滚任务到服务器
	 * @param tasks
	 */
	public void sendRollbackTaskInCommit(String gid, long time, SagaTransaction tasks) {
		RollbackInCommitTask rollbackInCommitTask = new RollbackInCommitTask(gid, time, tasks);
		rollbackInCommitTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), rollbackInCommitTask);
	}

	public void rollbackSuccess(String gid, long time) {
		RollbackSuccessTask rollbackSuccessTask = new RollbackSuccessTask(gid, time);
		rollbackSuccessTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), rollbackSuccessTask);

	}

	public void rollbackFailure(String gid, long time, SagaTransaction tasks) {
		RollbackFailureTask rollbackFailureTask = new RollbackFailureTask(gid, time, tasks);
		rollbackFailureTask.setAppName(level3Config.getAppName());
		level3Config.getTemplate().send(level3Config.getServerTopic(), rollbackFailureTask);

	}


}
