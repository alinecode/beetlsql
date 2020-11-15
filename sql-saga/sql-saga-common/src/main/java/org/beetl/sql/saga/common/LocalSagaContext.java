package org.beetl.sql.saga.common;

public class LocalSagaContext extends SagaContext {
	SagaTransaction transaction = null;
	public LocalSagaContext(){
		newTransaction();
	}

	/**
	 * 回滚所有操作
	 * 子类可以继承，以多次尝试回滚或者发送到队列（比如数据不可用），延迟回滚
	 */
	@Override
	public void rollback(){
		try{
			boolean success = transaction.rollback();
			if(!success){
				throw new SagaRollbackException("回滚失败");
			}

		}finally {
			newTransaction();
		}

	}

	@Override
	public SagaTransaction getTransaction(){
		return transaction;
	}

	protected  void newTransaction(){
		transaction = new LocalSagaTransaction();
	}
}
