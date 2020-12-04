package org.beetl.sql.saga.ms.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.beetl.sql.saga.common.SagaTransaction;
import org.beetl.sql.saga.ms.client.task.StartTask;
import org.beetl.sql.saga.ms.server.dao.RollbackMapper;
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
 * 用于操作数据库，保留回滚过程
 */
@Service
@Transactional
public class SagaManager {
	@Autowired
	RollbackMapper rollbackMapper;
	@Autowired
	ObjectMapper objectMapper;

	@Value("${beetlsql-saga.kafka.client-topic-preffix}")
	String clientTopicPrefix;

	@Autowired
	KafkaTemplate kafkaTemplate;

	public void addStartTask(String gid,long time,String appName){
		RollbackTaskEntity entity = new RollbackTaskEntity();
		entity.setAppName(appName);
		entity.setGid(gid);
		entity.setTime(time);
		entity.setCreateTime(System.currentTimeMillis());
		rollbackMapper.insert(entity);
	}

	/**
	 *  commit()
	 * @param gid
	 * @param time
	 * @param rollback
	 */
	public void addRollbackBySuccessCommit(String gid,long time,String appName, SagaTransaction rollback){
		updateRollbackTask(gid,time,appName,rollback);

	}

	/**
	 * rollback，同一个saga事务会有多个rollback。但判断如果当前rollback的time是最早的一条，则表示可以回滚整个rollback
	 * 否则，只是记录，并不回滚
	 * @param gid
	 * @param time
	 * @param rollback
	 */
	public void addRollbackAfterException(String gid,long time, String appName,SagaTransaction rollback){
		updateRollbackTask(gid,time,appName,rollback);
		int earlierCount= rollbackMapper.findEarlierTransaction(gid,time);
		if(earlierCount>0){
			return ;
		}
		//最后一个回滚，需要开始回滚了
		List<RollbackTaskEntity> list = rollbackMapper.allRollbackTask(gid);
		list.stream().forEach(rollbackTaskEntity -> {
			kafkaTemplate.send(clientTopicPrefix+"-"+rollbackTaskEntity.getAppName(),rollbackTaskEntity.getTaskInfo());
		});

	}

	protected  RollbackTaskEntity updateRollbackTask(String gid,long time, String appName,SagaTransaction rollback){
		RollbackTaskEntity template = new RollbackTaskEntity();
		template.setTime(time);
		template.setAppName(appName);
		template.setGid(gid);
		RollbackTaskEntity entity = rollbackMapper.templateOne(template);
		entity.setTaskInfo(rollback);
		entity.setStatus(BusinessStatus.Success);
		entity.setUpdateTime(System.currentTimeMillis());
		rollbackMapper.updateById(entity);
		return entity;
	}

	/**
	 * 微服务通知saga-server 回滚是否成功。
	 * @param gid
	 * @param time
	 * @param success
	 */
	public void notifyRollback(String gid,long time,boolean success,String appName){
		RollbackTaskEntity template = new RollbackTaskEntity();
		template.setTime(time);
		template.setAppName(appName);
		template.setGid(gid);
		RollbackTaskEntity entity = rollbackMapper.templateOne(template);
		if(success){
			entity.setRollbackStatus(RollbackStatus.Success);
		}else{
			entity.setRollbackStatus(RollbackStatus.Error);
		}
		entity.setUpdateTime(System.currentTimeMillis());
		rollbackMapper.updateById(entity);
		//TODO 是否需要检检测整个事务回滚成功，这里检测有问题，只能定时检测
		return  ;
	}
}
