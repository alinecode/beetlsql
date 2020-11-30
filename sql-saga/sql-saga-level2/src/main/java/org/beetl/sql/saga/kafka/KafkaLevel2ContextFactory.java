package org.beetl.sql.saga.kafka;

import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaContextFactory;

public class KafkaLevel2ContextFactory implements SagaContextFactory {
	KafkaLevel2Config config;
	public KafkaLevel2ContextFactory(KafkaLevel2Config config){
		this.config = config;
	}

	ThreadLocal<KafkaLevel2Context> local = new ThreadLocal(){
		@Override
        protected SagaContext initialValue(){
			return new KafkaLevel2Context(config);
		}
	};
	@Override
	public SagaContext current() {
		return local.get();
	}
}
