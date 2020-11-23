package org.beetl.sql.saga.common;


import java.util.concurrent.Callable;

public abstract class SagaContext {
	/**
	 * 特定框架必须实现SegaContextFactory，以及SegaContext子类
	 */
	public static SagaContextFactory sagaContextFactory = new SagaContextFactory() {
		@Override
        public LocalSagaContext current() {
			throw new UnsupportedOperationException("必须设置SegaContextFactory实现类,比如LocalSegaContextFactory");
		}
	};
	public abstract void rollback();
	public abstract SagaTransaction getTransaction();

	//支持除了jdbc访问数据库外，也可以把服务调用放到这里
	public abstract <T> T callService(Callable<T> callable, Runnable runnable) throws Exception;

}
