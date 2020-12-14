package org.beetl.sql.saga.ms.client.task;

import lombok.Data;

/**
 * 客户端(app)发送给Saga-server的需要备用的回滚任务，saga-server保留回滚任务，在一定条件下
 * 发送这些回滚任务到各自客户端进行实际回滚
 * @author xiandafu
 */
@Data
public class ClientTask implements   java.io.Serializable {
	String appName;
	String gid;
	long time;

	public ClientTask(String appName, String gid, long time) {
		this.appName = appName;
		this.gid = gid;
		this.time = time;
	}

	public ClientTask(){
		//反序列化用
	}
}
