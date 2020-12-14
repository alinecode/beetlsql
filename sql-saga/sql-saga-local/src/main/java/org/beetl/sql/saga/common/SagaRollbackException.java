package org.beetl.sql.saga.common;

public class SagaRollbackException extends RuntimeException {
	public SagaRollbackException(String message ){
		super(message);
	}

}
