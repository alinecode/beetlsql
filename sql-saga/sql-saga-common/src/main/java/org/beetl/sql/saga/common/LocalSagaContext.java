package org.beetl.sql.saga.common;

public class LocalSagaContext extends SagaContext {
	SagaTransaction transaction = null;
	public LocalSagaContext(){
		transaction = new LocalSagaTransaction();
	}

	/**
	 * 回滚所有操作
	 * 子类可以继承，以多次尝试回滚或者发送到队列（比如数据不可用），延迟回滚
	 */
	@Override
	public void rollback(){
		boolean success = transaction.rollback();
		if(!success){
			throw new SagaRollbackException("回滚失败");
		}

	}

	@Override
	public SagaTransaction getTransaction(){
		return transaction;
	}
}
