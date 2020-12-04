package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.beetl.sql.saga.common.SagaTransaction;

/**
 * 发送到saga-server 开始任务，表示回滚失败，saga-server应该尝试再次发送（重试，或者定时，或者手工)
 * @author xiandafu
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackFailureTask extends RollbackInCommitTask {
	String gid;
	long time;
	public RollbackFailureTask(String gid,long time, SagaTransaction rollback){
		super(gid,time,rollback);

	}

	public RollbackFailureTask(){
		//序列化用
	}

}
