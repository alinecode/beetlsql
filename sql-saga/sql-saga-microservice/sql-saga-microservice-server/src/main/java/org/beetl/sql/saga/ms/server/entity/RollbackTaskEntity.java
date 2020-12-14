package org.beetl.sql.saga.ms.server.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.saga.common.SagaTransaction;
import org.beetl.sql.saga.ms.server.util.BusinessStatus;
import org.beetl.sql.saga.ms.server.util.RollbackStatus;

@Table(name="rollback_task")
@Data
public class RollbackTaskEntity {
	@AssignID("uuid")
	String id;
	String gid;
	// 应用名称，事务管理也会将此作为topic 把回滚内容发送回应用侧
	String appName;
	BusinessStatus status;
	RollbackStatus rollbackStatus;
	Long time;
	String taskInfo;
	Long createTime;
	Long updateTime;
}
