package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
/**
 * 发送到saga-server 开始任务
 * @author xiandafu
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class StartClientTask extends ClientTask {
	String gid;
	long time;
	public StartClientTask(String gid,long time){
		this.gid = gid;
		this.time = time;

	}

	public StartClientTask(){
		//序列化用
	}

}
