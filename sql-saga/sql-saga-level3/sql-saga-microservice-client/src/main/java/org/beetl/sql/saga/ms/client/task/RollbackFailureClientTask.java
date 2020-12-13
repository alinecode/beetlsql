package org.beetl.sql.saga.ms.client.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.beetl.sql.saga.ms.client.SagaLevel3Transaction;

import java.util.Objects;

/**
 * 发送到saga-server 开始任务，表示回滚失败，saga-server应该尝试再次发送（重试，或者定时，或者手工)
 * @author xiandafu
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@Clazz")
public class RollbackFailureClientTask extends RollbackInCommitClientTask {
	String gid;
	long time;
	public RollbackFailureClientTask(String gid,long time, SagaLevel3Transaction rollback){
		super(gid,time,rollback);

	}

	public RollbackFailureClientTask(){
		//序列化用
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		RollbackFailureClientTask that = (RollbackFailureClientTask) o;
		return time == that.time && gid.equals(that.gid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), gid, time);
	}
}
