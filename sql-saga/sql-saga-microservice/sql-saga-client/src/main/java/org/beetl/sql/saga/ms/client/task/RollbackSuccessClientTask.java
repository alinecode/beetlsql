package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发送到saga-server ，表示回滚成功。saga收到此消息后，无需再像此app发送回滚任务
 * @author xiandafu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@Clazz")
public class RollbackSuccessClientTask extends ClientTask {

	public RollbackSuccessClientTask(String appName, String gid, long time) {
		super(appName, gid, time);

	}

	public RollbackSuccessClientTask() {
		//序列化用
	}

}
