package org.beetl.sql.saga.kafka;

import org.beetl.sql.saga.common.SagaTransaction;

import java.util.List;

/**
 * 与saga-server 交互的api
 */
public class SagaServerClient {
	SagaLevel3Config level3Config;
	public SagaServerClient(SagaLevel3Config level3Config){
		this.level3Config = level3Config;
	}

	/**
	 * 标记开始
	 * @param gid
	 * @param time
	 */
	public void start(String gid,long time){
		level3Config.getTemplate().send(level3Config.getServerTopic(),new StartTask(gid,time));
	}


	/**
	 * 发送回滚任务到服务器
	 * @param tasks
	 */
	public void sendTransactionTask(String gid,long time,SagaTransaction  tasks,boolean localSuccess){
		level3Config.getTemplate().send(level3Config.getServerTopic(),new RollbackTask(gid,time,tasks,localSuccess));
	}



}
