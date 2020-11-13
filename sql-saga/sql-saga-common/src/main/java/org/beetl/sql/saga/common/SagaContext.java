package org.beetl.sql.saga.common;

public abstract class SagaContext {
	/**
	 * 特定框架必须实现SegaContextFactory，以及SegaContext子类
	 */
	public static SagaContextFactory sagaContextFactory = new SagaContextFactory() {
		public LocalSagaContext current() {
			throw new UnsupportedOperationException("必须设置SegaContextFactory实现类,比如LocalSegaContextFactory");
		}
	};
	public abstract void rollback();
	public abstract SagaTransaction getTransaction();
}
