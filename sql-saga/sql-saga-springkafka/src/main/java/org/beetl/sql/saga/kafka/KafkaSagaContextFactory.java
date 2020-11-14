package org.beetl.sql.saga.kafka;

import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaContextFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaSagaContextFactory implements SagaContextFactory {
	KafkaSagaConfig config;
	public KafkaSagaContextFactory(KafkaSagaConfig config){
		this.config = config;
	}

	ThreadLocal<KafkaSagaContext> local = new ThreadLocal(){
		protected SagaContext initialValue(){
			return new KafkaSagaContext(config);
		}
	};
	@Override
	public SagaContext current() {
		return local.get();
	}
}
