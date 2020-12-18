package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.saga.ms.client.SagaClientTransaction;

/**
 * 发送到saga-server的回滚任务，
 * @author xiandafu
 */
@Data
@EqualsAndHashCode(callSuper = true )
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackClientTask extends RollbackInCommitClientTask {

	public RollbackClientTask(String appName,String gid,long time, SagaClientTransaction rollback) {
		super(appName,gid,time,rollback);
	}

	public RollbackClientTask() {
		//序列化用
	}

}
