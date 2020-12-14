package org.beetl.sql.saga.ms.client.task;

import lombok.Data;

/**
 * saga-server发送给客户端的回滚指令，客户端接收到此后，开始真正的回滚
 */
@Data
public class Server2ClientRollbackTask {
	private String serverTaskId;
	private String gid;
	private long time;
	//回滚任务，是SagaLevel3Transaction，没有使用对象而是字符串原因是saga-server不可能反序列化客户端成对象
	String taskInfo;
}
