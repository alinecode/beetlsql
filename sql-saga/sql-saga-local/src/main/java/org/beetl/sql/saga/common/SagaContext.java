package org.beetl.sql.saga.common;


import java.util.concurrent.Callable;

/**
 * 事务管理，负责记录事物嵌套关系和事物边界，由子类通过time和nested实现
 * @author xiandafu
 */
public abstract class SagaContext {
	/**
	 * 事务唯一id，通常是业务id号,能唯一的标识全局事务
	 * 对于local的saga模式来说，gid不需要，gid仅仅用于微服务
	 * */
	protected String gid;
	/**
	 * 事务的开始时间。对于对于local的saga模式来说，此time无实际意义
	 * 但对于微服务来说，time标识了业务的先后顺序，从而能识别事务的边界。saga-server收到最小的time 回滚任务，则表示真的需要发起回滚了，已经到了事务边界了。
	 * 不同于其他saga实现有显示的开始和结束。beetlsql的saga-server通过时间自动判断，更加科学(显示的申明saga开始和结束有问题，因为此服务本生也随着业务变化会在其他saga事务里，不利于嵌套）
	 * 比如，微服务A调用了微服务B，因此A的time肯定是小于B的time，如果B出错并标记回滚，Saga—Server发现还没有到事务边界，不会操作。等B抛出异常到A后，A调用回滚
	 * 因为边界在A，所以A发起真正回滚
	 */
	protected Long time = -1L;

	transient  protected Nested nested = new Nested();


	/**
	 * 特定框架必须实现SegaContextFactory，以及SegaContext子类
	 */
	public static SagaContextFactory sagaContextFactory = new SagaContextFactory() {
		@Override
		public LocalSagaContext current() {
			throw new UnsupportedOperationException("必须设置SegaContextFactory实现类,比如LocalSegaContextFactory");
		}
	};


	public abstract void start();

	public  abstract void start(String gid);

	public abstract   void rollback();

	/**
	 * 提交，对于分库操作，无需任何commit，但是，如果是微服务，commit要发送rollback到全局事务控制器，等待可能的回滚
	 */
	public  void commit(){
		nested.exit();
	}


	public abstract SagaTransaction getTransaction();

	//自定义复杂的回滚操作
	public abstract <T> T callService(Callable<T> callable, Runnable rollback) throws Exception;

	public SagaContext setGid(String gid){
		this.gid = gid;
		return this;
	}

	public SagaContext setTime(Long time) {
		this.time = time;
		return this;
	}

}
