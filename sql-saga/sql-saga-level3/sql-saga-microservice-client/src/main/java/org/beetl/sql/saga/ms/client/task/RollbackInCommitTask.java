package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.beetl.sql.saga.common.SagaTransaction;

/**
 * 发送到saga-server的回滚任务,是在提交阶段发送的，记录需要回滚
 * @author xiandafu
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackInCommitTask extends StartTask {
	SagaTransaction rollback;

	public RollbackInCommitTask(String gid,long time,SagaTransaction rollback) {
		super(gid,time);
		this.rollback = rollback;
	}

	public RollbackInCommitTask() {
		//序列化用
	}

}
