package org.beetl.sql.sega.common;

public class SegaContext {
	/**
	 * 特定框架必须实现SegaContextFactory，以及SegaContext子类
	 */
	public static SegaContextFactory segaContextFactory = new SegaContextFactory() {
		ThreadLocal<SegaContext> local = new ThreadLocal(){
			protected  SegaContext initialValue(){
				return new SegaContext();
			}
		};
		@Override
		public SegaContext current() {
			return local.get();
		}

		@Override
		public void clear() {
			local.remove();
		}
	};

	SegaTransaction transaction = new SegaTransaction();
	/**
	 * 回滚所有操作
	 * 子类可以继承，以多次尝试回滚或者发送到队列（比如数据不可用），延迟回滚
	 */
	public void rollback(){
		boolean success = transaction.rollback();
		if(!success){
			throw new SegaRollbackException("回滚失败");
		}

	}
	public SegaTransaction getTransaction(){
		return transaction;
	}
}
