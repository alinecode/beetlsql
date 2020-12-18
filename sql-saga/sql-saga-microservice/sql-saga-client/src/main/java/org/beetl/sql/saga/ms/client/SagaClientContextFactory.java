package org.beetl.sql.saga.ms.client;

import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaContextFactory;

public class SagaClientContextFactory implements SagaContextFactory {
	SagaClientConfig config;
	public SagaClientContextFactory(SagaClientConfig config){
		this.config = config;
	}

	ThreadLocal<SagaClientContext> local = new ThreadLocal(){
		@Override
        protected SagaContext initialValue(){
			return new SagaClientContext(config);
		}
	};
	@Override
	public SagaContext current() {
		return local.get();
	}
}
