package org.beetl.sql.sega.kafka;

import lombok.Data;
import org.beetl.sql.sega.common.LocalSegaTransaction;
import org.beetl.sql.sega.common.SegaRollbackTask;
import org.beetl.sql.sega.common.SegaTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class KafkaSegaTransaction extends LocalSegaTransaction {
	int totalTry = 0;
	@Override
	public boolean rollback(){
		boolean success = super.rollback();
		if(!success){
			totalTry++;
		}
		return success;
	}

}
