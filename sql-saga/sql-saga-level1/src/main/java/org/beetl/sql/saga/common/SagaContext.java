package org.beetl.sql.saga.common;


import java.util.concurrent.Callable;

/**
 * 事务管理，暂时没有考虑本地嵌套
 */
public abstract class SagaContext {

	protected Object businessKey;
	protected String gid;
	protected Long time;
	/**
	 * 特定框架必须实现SegaContextFactory，以及SegaContext子类
	 */
	public static SagaContextFactory sagaContextFactory = new SagaContextFactory() {
		@Override
		public LocalSagaContext current() {
			throw new UnsupportedOperationException("必须设置SegaContextFactory实现类,比如LocalSegaContextFactory");
		}
	};

	public  void start(){
		time= System.nanoTime();
	}
	public  void start(String gid){
		this.gid = gid;
		this.time = time;
	}

	public abstract void rollback();

	/**
	 * 提交，对于分库操作，无需任何commit，但是，如果是微服务，commit要发送rollback到全局事务控制器，等待可能的回滚
	 */
	public  void commit(){
		//do nothing
	}

	public abstract SagaTransaction getTransaction();

	//自定义复杂的回滚操作
	public abstract <T> T callService(Callable<T> callable, Runnable rollback) throws Exception;

	public SagaContext setGid(Object businessKey){
		this.businessKey = businessKey;
		return this;
	}

}
