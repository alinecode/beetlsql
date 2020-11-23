package org.beetl.sql.saga.common;

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
