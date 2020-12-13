package org.beetl.sql.saga.ms.client.task;

import lombok.Data;

/**
 * 客户端(app)发送给Saga-server的回滚任务
 */
@Data
public class ClientTask implements   java.io.Serializable {
	String appName;

}
