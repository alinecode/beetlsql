package org.beetl.sql.saga.ms.server.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.saga.common.SagaTransaction;
import org.beetl.sql.saga.ms.server.util.BusinessStatus;
import org.beetl.sql.saga.ms.server.util.Jackson;
import org.beetl.sql.saga.ms.server.util.RollbackStatus;

@Table(name="rollback")
@Data
public class RollbackEntity {
	@AssignID
	String gid;
	RollbackStatus rollbackStatus;
	Long createTime;
	Long updateTime;
	String firstAppName;
	Integer total;
	Integer success;
}
