package org.beetl.sql.saga.kafka;

import org.beetl.sql.saga.common.LocalSagaContext;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaTransaction;

import java.util.concurrent.Callable;

/**
 * 回滚事务，如果没有完全成功，则发送到kafka队列，在尝试多次后，仍然没有成功，发送给
 *
 * @see  SagaLevel3Config
 * @author xiandafu
 */
public class SagaLevel3Context extends SagaContext {
	SagaLevel3Transaction transaction = null;
	SagaLevel3Config config;
	SagaServerClient client = null;

	public SagaLevel3Context(SagaLevel3Config config) {
		newTransaction();
		this.config = config;
		client = new SagaServerClient(config);
	}

	public SagaLevel3Context(SagaLevel3Transaction transaction, SagaLevel3Config config) {
		this.transaction = transaction;
		this.config = config;
		client = new SagaServerClient(config);
	}

	public void start() {
		throw new IllegalArgumentException("必须提供gid");
	}

	public void start(String gid) {
		super.start(gid);
		client.start(gid, this.time);
	}

	public void commit() {
		client.sendTransactionTask(gid,time,transaction, true);
		transaction.getTasks().clear();
		newTransaction();
	}

	@Override
	public void rollback() {
		client.sendTransactionTask(gid,time,transaction, false);
		transaction.getTasks().clear();
		newTransaction();
	}

	/**
	 * 真正的本地回滚
	 */
	public void realRollback() {
		boolean success = transaction.rollback();
		if (success) {
			return;
		}
		rollback();

	}

	@Override
	public SagaTransaction getTransaction() {
		return transaction;
	}

	protected void newTransaction() {
		transaction = new SagaLevel3Transaction();
	}

	/**
	 * 服务调用也放入context管理
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
