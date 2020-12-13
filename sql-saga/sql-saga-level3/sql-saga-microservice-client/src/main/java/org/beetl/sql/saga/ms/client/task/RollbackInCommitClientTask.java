package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.saga.ms.client.SagaLevel3Transaction;

/**
 * 发送到saga-server的回滚任务,是在提交阶段发送的，记录需要回滚
 * @author xiandafu
 */
@Data
@EqualsAndHashCode(callSuper = true )
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackInCommitClientTask extends StartClientTask {


	SagaLevel3Transaction rollback;

	public RollbackInCommitClientTask(String gid,long time, SagaLevel3Transaction rollback) {
		super(gid,time);
		this.rollback = rollback;
	}

	public RollbackInCommitClientTask() {
		//序列化用
	}

}
