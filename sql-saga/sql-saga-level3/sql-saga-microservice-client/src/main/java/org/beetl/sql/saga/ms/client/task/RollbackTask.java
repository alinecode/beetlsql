package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.beetl.sql.saga.common.SagaTransaction;

/**
 * 发送到saga-server的回滚任务，
 * @author xiandafu
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackTask extends RollbackInCommitTask {

	public RollbackTask(String gid,long time,SagaTransaction rollback) {
		super(gid,time,rollback);
	}

	public RollbackTask() {
		//序列化用
	}

}
