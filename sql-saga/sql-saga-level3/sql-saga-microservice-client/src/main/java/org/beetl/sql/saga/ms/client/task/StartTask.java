package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
/**
 * 发送到saga-server 开始任务
 * @author xiandafu
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class StartTask extends Task {
	String gid;
	long time;
	public 	StartTask(String gid,long time){
		this.gid = gid;
		this.time = time;

	}

	public 	StartTask(){
		//序列化用
	}

}
