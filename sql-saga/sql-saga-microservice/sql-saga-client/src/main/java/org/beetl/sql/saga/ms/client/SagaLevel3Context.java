package org.beetl.sql.saga.ms.client;

import org.beetl.sql.saga.common.LocalSagaContext;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaTransaction;

import java.util.concurrent.Callable;

/**
 * 回滚事务，如果没有完全成功，则发送到kafka队列，在尝试多次后，仍然没有成功，发送给
 *
 * @see  SagaLevel3ClientConfig
 * @author xiandafu
 */
public class SagaLevel3Context extends SagaContext {
	SagaLevel3Transaction transaction = null;
	SagaLevel3ClientConfig config;
	SagaServerClient client = null;

	public SagaLevel3Context(SagaLevel3ClientConfig config) {
		newTransaction();
		this.config = config;
		client = new SagaServerClient(config);
	}

	public SagaLevel3Context(SagaLevel3Transaction transaction, SagaLevel3ClientConfig config) {
		this.transaction = transaction;
		this.config = config;
		client = new SagaServerClient(config);

	}

	public void start() {
		throw new IllegalArgumentException("微服务必须提供gid");
	}

	public void start(String gid) {
		if(this.gid!=null){
			/**
			 * 只能在当前微服务开始处有一个start，不能在微服务实现里有多个start，
			 * 这不同于传统的事务上下文有传播机制
			 */
			throw new IllegalStateException("不能在单进程里嵌套使用Saga事务");
		}
		super.start(gid);
		try{
			client.start(gid, time);
		}catch (Exception ex){
			clear();
			throw new IllegalStateException("事务管理器不可用 "+ex.getMessage());

		}

	}

	public void commit() {
		try{
			client.sendRollbackTaskInCommit(gid,time,transaction);

		}catch (Exception ex){
			throw new IllegalStateException("事务管理器不可用 "+ex.getMessage());
		}finally {
			clear();
		}
	}

	@Override
	public void rollback() {
		try{
			//仅仅发送回滚任务，真正回滚需要等待收到saga-server通知，然后调用realRollback
			client.sendRollbackTask(gid,time,transaction);
		}catch (Exception ex){
			throw new IllegalStateException("事务管理器不可用 "+ex.getMessage());
		}finally {
			clear();
		}

	}

	/**
	 * 真正的本地回滚
	 */
	public boolean realRollback() {
		try{
			boolean success = transaction.rollback();
			if (success) {
				client.rollbackSuccess(gid,time);
				return true;
			}
			client.rollbackFailure(gid,time,transaction);
			return false;
		}catch(Exception ex){
			//不可能到这里
			throw new IllegalStateException(ex);
		}

	}

	@Override
	public SagaTransaction getTransaction() {
		return transaction;
	}

	protected void newTransaction() {
		transaction = new SagaLevel3Transaction();
	}

	protected  void clear(){
		transaction.getTasks().clear();
		this.setGid(null);
		this.setTime(0L);
		newTransaction();
	}

	/**
	 * 非sql类的，比如批量增加数据，逆向操作可能只是简单的删除外键，而不需要每条都删除
	 * @param callable
	 * @param runnable ，必须保证可被json序列化和反序列化，比如，提供一个空的构造函数
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	@Override
	public <T> T callService(Callable<T> callable, Runnable runnable) throws Exception {
		try {
			return callable.call();
		} catch (Exception ex) {
			this.getTransaction().addTask(new LocalSagaContext.FunctionCallback(runnable));
			throw ex;
		}
	}


}
