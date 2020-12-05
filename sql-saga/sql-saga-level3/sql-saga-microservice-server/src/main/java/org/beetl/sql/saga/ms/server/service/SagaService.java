package org.beetl.sql.saga.ms.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.common.SagaRollbackTask;
import org.beetl.sql.saga.common.SagaTransaction;
import org.beetl.sql.saga.ms.server.dao.RollbackMapper;
import org.beetl.sql.saga.ms.server.dao.RollbackTaskMapper;
import org.beetl.sql.saga.ms.server.entity.RollbackEntity;
import org.beetl.sql.saga.ms.server.entity.RollbackTaskEntity;
import org.beetl.sql.saga.ms.server.util.BusinessStatus;
import org.beetl.sql.saga.ms.server.util.RollbackStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * web管理用
 */
@Service
@Transactional
@Slf4j
public class SagaService {
	@Autowired
	RollbackTaskMapper rollbackTaskMapper;

	@Autowired
	RollbackMapper rollbackMapper;
	@Autowired
	ObjectMapper objectMapper;

	public List<RollbackTaskEntity> allSagaRollbackTask(){
		List<RollbackTaskEntity> list = rollbackTaskMapper.createLambdaQuery().desc(RollbackTaskEntity::getCreateTime).select();
		return list;
	}

	public List<RollbackEntity> allSagaRollback(){
		List<RollbackEntity> list = rollbackMapper.createLambdaQuery().desc(RollbackEntity::getCreateTime)
				.select();
		return list;
	}
}
