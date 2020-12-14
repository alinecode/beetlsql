package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.saga.ms.client.SagaLevel3Transaction;

import java.util.Objects;

/**
 * 发送到saga-server 开始任务，表示回滚失败，saga-server应该尝试再次发送（重试，或者定时，或者手工)
 * @author xiandafu
 */
@Data
@EqualsAndHashCode(callSuper = true )
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackFailureClientTask extends RollbackInCommitClientTask {
	public RollbackFailureClientTask(String appName,String gid,long time, SagaLevel3Transaction rollback){
		super(appName,gid,time,rollback);

	}

	public RollbackFailureClientTask(){
		//序列化用
	}

}
