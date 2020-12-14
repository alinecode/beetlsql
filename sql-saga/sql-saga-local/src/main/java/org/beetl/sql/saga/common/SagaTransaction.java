package org.beetl.sql.saga.common;

/**
 * 必须实现序列化或者被json工具序列化
 */
public interface SagaTransaction extends  java.io.Serializable {

	/**
	 * 添加回滚操作
	 * @param task
	 */
	void addTask(SagaRollbackTask task);

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





}
