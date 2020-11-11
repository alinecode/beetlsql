package org.beetl.sql.sega.common;

import java.util.List;

public interface SegaTransaction {
	/**
	 * 事务唯一id
	 * @return
	 */
	String getSegaTransactionId();

	/**
	 * 添加回滚操作
	 * @param task
	 */
	void addTask(SegaRollbackTask task);

	/**
	 * 执行回滚
	 * @return
	 */
	boolean rollback();

	/**
	 * 所有回滚操作是否成功
	 * @return
	 */
	boolean isSuccess();

	/**
	 * 回滚失败任务
	 * @return
	 */
	List<SegaRollbackTask> failureTaskAfterRollBack();
}
