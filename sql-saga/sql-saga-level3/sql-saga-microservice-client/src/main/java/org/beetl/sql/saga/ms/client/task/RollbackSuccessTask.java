package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * 发送到saga-server ，表示回滚成功。saga收到此消息后，无需再像此app发送回滚任务
 * @author xiandafu
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackSuccessTask extends Task {
	String gid;
	long time;
	public RollbackSuccessTask(String gid,long time){
		this.gid = gid;
		this.time = time;

	}

	public RollbackSuccessTask(){
		//序列化用
	}

}
