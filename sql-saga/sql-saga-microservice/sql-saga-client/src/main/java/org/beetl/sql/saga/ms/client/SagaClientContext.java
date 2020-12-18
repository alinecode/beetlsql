package org.beetl.sql.saga.ms.client;

import org.beetl.sql.saga.common.LocalSagaContext;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaTransaction;

import java.util.concurrent.Callable;

/**
 * 回滚事务，如果没有完全成功，则发送到kafka队列，在尝试多次后，仍然没有成功，发送给
 *
 * @see  SagaClientConfig
 * @author xiandafu
 */
public class SagaClientContext extends SagaContext {
	SagaClientTransaction transaction = null;
	SagaClientConfig config;
	SagaServerApi client = null;

	public SagaClientContext(SagaClientConfig config) {
		transaction = new SagaClientTransaction();
		this.config = config;
		client = new SagaServerApi(config);
	}

	/**
	 * 创建一个临时
	 * @param transaction
	 * @param config
	 * @return
	 */
	public static SagaClientContext tempContext(SagaClientTransaction transaction, SagaClientConfig config){
		SagaClientContext sagaClientContext = new SagaClientContext(transaction,config);
		return sagaClientContext;
	}

	private SagaClientContext(SagaClientTransaction transaction, SagaClientConfig config) {
		this.transaction = transaction;
		this.config = config;
		client = new SagaServerApi(config);

	}

	public void start() {
		throw new IllegalArgumentException("微服务必须提供gid");
	}

	public void start(String gid) {
		if(!nested.isRoot()){
			if(!this.gid.equals(gid)){
				throw new IllegalStateException("gid 必须一致 期望"+this.gid+" 但是 "+gid);
			}
			return ;
		}
		time= System.nanoTime();
		nested.enter();
		this.gid = gid;
		try{
			client.start(gid, time);
		}catch (Exception ex){
			clear();
			throw new IllegalStateException("事务管理器不可用 "+ex.getMessage());

		}

	}

	public void commit() {
		try{
			nested.exit();
			if(!nested.isRoot()){
				return ;
			}
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
			nested.exit();
			if(!nested.isRoot()){
				return ;
			}
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



	protected  void clear(){
		if(!nested.isRoot()){
			return ;
		}
		this.setGid(null);
		this.setTime(-1L);
		this.transaction = new SagaClientTransaction();
	}

	/**
	 * 非sql类的，比如批量增加数据，逆向操作可能只是简单的删除外键，而不需要每条都删除
	 * @param callable
	 * @param runnable ，必须保证callable和runnable可被json序列化和反序列化
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
