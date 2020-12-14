package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发送到saga-server 开始任务
 * @author xiandafu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class StartClientTask extends ClientTask {

	public StartClientTask(String appName, String gid, long time){
		super(appName,gid,time);
	}

	public StartClientTask(){
		//序列化用
	}

}
