package org.beetl.sql.saga.kafka;

import lombok.Data;
import org.beetl.sql.saga.common.LocalSagaTransaction;

@Data
public class KafkaSagaTransaction extends LocalSagaTransaction {
	@Override
	public boolean rollback(){
		boolean success = super.rollback();
		if(!success){
			totalTry++;
		}
		return success;
	}

}
