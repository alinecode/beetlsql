package org.beetl.sql.saga.kafka;

import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaContextFactory;

public class SagaLevel3ContextFactory implements SagaContextFactory {
	SagaLevel3Config config;
	public SagaLevel3ContextFactory(SagaLevel3Config config){
		this.config = config;
	}

	ThreadLocal<SagaLevel3Context> local = new ThreadLocal(){
		@Override
        protected SagaContext initialValue(){
			return new SagaLevel3Context(config);
		}
	};
	@Override
	public SagaContext current() {
		return local.get();
	}
}
