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

	@Autowired
	SagaManager sagaManager;

	public List<RollbackTaskEntity> allSagaRollbackTask(){
		List<RollbackTaskEntity> list = rollbackTaskMapper.createLambdaQuery().desc(RollbackTaskEntity::getCreateTime).select();
		return list;
	}

	public List<RollbackEntity> allSagaRollback(){
		List<RollbackEntity> list = rollbackMapper.createLambdaQuery().desc(RollbackEntity::getCreateTime)
				.select();
		return list;
	}

	public List<RollbackEntity> allNotSuccessRollback(){
		List<RollbackEntity> list = rollbackMapper.createLambdaQuery().desc(RollbackEntity::getCreateTime)
				.andNotEq(RollbackEntity::getRollbackStatus,RollbackStatus.Success).select();
		return list;
	}

	/**
	 * 得到事务的所有回滚任务明细
	 * @param gid
	 * @return
	 */
	public List<RollbackTaskEntity> getSagaRollbackDetail(String gid){
		List<RollbackTaskEntity> list = rollbackTaskMapper.createLambdaQuery().desc(RollbackTaskEntity::getCreateTime)
				.andEq(RollbackTaskEntity::getGid,gid).select();
		return list;
	}

	/**
	 * 执行某个回滚任务
	 * @param taskId
	 */
	public void  rollbackTaskId(String taskId){
		RollbackTaskEntity entity = rollbackTaskMapper.unique(taskId);
		if(entity.getRollbackStatus()!=RollbackStatus.Error){
			return ;
		}
		sagaManager.doRollback(entity);

	}

	/**
	 * 回滚事务中所有未成功执行的任务
	 * @param gid
	 */
	public void  rollback(String gid){
		List<RollbackTaskEntity> list = rollbackTaskMapper.createLambdaQuery().andEq(RollbackTaskEntity::getId,gid)
				.andEq(RollbackTaskEntity::getRollbackStatus,RollbackStatus.Error)
				.desc(RollbackTaskEntity::getTime).select();
		list.forEach(rollbackTaskEntity -> {
			sagaManager.doRollback(rollbackTaskEntity);
		});
	}

	/**
	 * 强制回滚事务
	 * @param gid
	 */
	public void  forceRollback(String gid){
		List<RollbackTaskEntity> list = rollbackTaskMapper.createLambdaQuery().andEq(RollbackTaskEntity::getId,gid)
				.desc(RollbackTaskEntity::getTime).select();
		list.forEach(rollbackTaskEntity -> {
			sagaManager.doRollback(rollbackTaskEntity);
		});
	}

}
