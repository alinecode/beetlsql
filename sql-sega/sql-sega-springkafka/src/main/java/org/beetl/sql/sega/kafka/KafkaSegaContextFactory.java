package org.beetl.sql.sega.kafka;

import org.beetl.sql.sega.common.LocalSegaContext;
import org.beetl.sql.sega.common.SegaContext;
import org.beetl.sql.sega.common.SegaContextFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaSegaContextFactory implements SegaContextFactory {
	@Autowired
	KafkaSegaConfig config;
	ThreadLocal<KafkaSegaContext> local = new ThreadLocal(){
		protected SegaContext initialValue(){
			return new KafkaSegaContext(config);
		}
	};
	@Override
	public SegaContext current() {
		return local.get();
	}
}
