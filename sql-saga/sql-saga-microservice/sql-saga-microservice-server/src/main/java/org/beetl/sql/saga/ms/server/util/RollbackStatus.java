package org.beetl.sql.saga.ms.server.util;

/**
 * 回滚是否成功,
 */
public enum RollbackStatus {
	INIT,//初始化
	Ongoing,//开始回滚
	Success,//回滚成功
	Error //回滚失败
}
