package org.beetl.sql.saga.common;

import lombok.Data;

import java.util.concurrent.Callable;

public class LocalSagaContext extends SagaContext {
	SagaTransaction transaction = null;
	public LocalSagaContext(){
		newTransaction();
	}



	@Override
	public void start(){
		if(nested.isRoot()){
			time= System.nanoTime();
		}
		nested.enter();
	}

	/**
	 * 对于local-saga，gid参数不是需要强制传入。
	 * @param gid
	 */
	@Override
	public void start(String gid){
		start();
		this.gid = gid;
	}



	/**
	 * 回滚所有操作
	 * 子类可以继承，以多次尝试回滚或者发送到队列（比如数据不可用），延迟回滚
	 */
	@Override
	public void rollback(){
		nested.exit();
		if(!nested.isRoot()){
			return ;
		}

		try{
			boolean success = transaction.rollback();
			//用户可以扩展，提供多次回滚机会而不是只回滚一次
			if(!success){
				throw new SagaRollbackException("回滚失败");
			}

		}finally {
			newTransaction();
		}

	}


	public  void commit(){
		super.commit();
		if(nested.isRoot()){
			newTransaction();
		}
	}

	@Override
	public SagaTransaction getTransaction(){
		return transaction;
	}

	protected  void newTransaction(){
		this.gid=null;
		this.time=1L;
		transaction = new LocalSagaTransaction();
	}


	/**
	 * 支持除了jdbc访问数据库外，也可以把服务调用放到这里
	 * 统一处理回滚
	 */
	@Override
	public <T> T callService(Callable<T> callable, Runnable runnable) throws Exception{
		try{
			return callable.call();
		}catch(Exception ex){
			this.getTransaction().addTask(new FunctionCallback(runnable));
			throw ex;
		}
	}

	@Data
	public static class FunctionCallback implements  SagaRollbackTask{

		Runnable function;
		public FunctionCallback(Runnable function){
			this.function = function;
		}

		@Override
		public boolean call() {
			try{
				function.run();
				return true;
			}catch (Exception ex){
				return false;
			}
		}
	}

}
