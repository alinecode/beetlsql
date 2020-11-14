package org.beetl.sql.saga.kafka;

import org.beetl.sql.saga.common.SagaTransaction;

public interface RollbackCoder {
	public Object encode(SagaTransaction obj);
	public SagaTransaction decode(Object obj);
}
