package org.beetl.sql.sega.common;

public class LocalSegaContext extends SegaContext {
	SegaTransaction transaction = null;
	public LocalSegaContext(){
		transaction = new LocalSegaTransaction();
	}

	/**
	 * 回滚所有操作
	 * 子类可以继承，以多次尝试回滚或者发送到队列（比如数据不可用），延迟回滚
	 */
	@Override
	public void rollback(){
		boolean success = transaction.rollback();
		if(!success){
			throw new SegaRollbackException("回滚失败");
		}

	}

	@Override
	public SegaTransaction getTransaction(){
		return transaction;
	}
}
