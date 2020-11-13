package org.beetl.sql.saga.common;

public class LocalSagaContextFactory implements SagaContextFactory {
	ThreadLocal<LocalSagaContext> local = new ThreadLocal(){
		protected LocalSagaContext initialValue(){
			return new LocalSagaContext();
		}
	};
	@Override
	public LocalSagaContext current() {
		return local.get();
	}
}
