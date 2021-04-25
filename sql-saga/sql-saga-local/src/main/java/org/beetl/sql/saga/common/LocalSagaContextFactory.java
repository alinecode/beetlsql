package org.beetl.sql.saga.common;

/**
 * 实现SagaContextFactory，仅用于管理本地的多库saga事物
 * @author xiandafu
 */
public class LocalSagaContextFactory implements SagaContextFactory {
	static ThreadLocal<LocalSagaContext> local = new ThreadLocal(){
		@Override
        protected LocalSagaContext initialValue(){
			LocalSagaContext context =  new LocalSagaContext();
			return context;
		}
	};
	@Override
	public LocalSagaContext current() {
		return local.get();
	}

}
