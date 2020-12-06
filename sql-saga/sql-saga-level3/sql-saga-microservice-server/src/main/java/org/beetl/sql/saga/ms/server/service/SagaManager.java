package org.beetl.sql.saga.ms.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
 * 用于操作数据库，保留回滚过程
 */
@Service
@Transactional
@Slf4j
public class SagaManager {
	@Autowired
	RollbackTaskMapper rollbackTaskMapper;

	@Autowired
	RollbackMapper rollbackMapper;
	@Autowired
	ObjectMapper objectMapper;

	@Value("${beetlsql-saga.kafka.client-topic-preffix}")
	String clientTopicPrefix;

	@Autowired
	KafkaTemplate kafkaTemplate;

	/**
	 * start()
	 * @param gid
	 * @param time
	 * @param appName
	 */
	public void addStartTask(String gid,long time,String appName){

		RollbackEntity rollbackEntity = rollbackMapper.single(gid);
		if(rollbackEntity==null){
			rollbackEntity = new RollbackEntity();
			rollbackEntity.setGid(gid);
			rollbackEntity.setFirstAppName(appName);
			rollbackEntity.setCreateTime(System.currentTimeMillis());
			rollbackEntity.setTotal(null);
			rollbackEntity.setRollbackStatus(RollbackStatus.INIT);
			rollbackEntity.setSuccess(0);
			rollbackMapper.insert(rollbackEntity);
			log.info("start gid trans for "+gid+" from "+appName);
		}
		RollbackTaskEntity entity = new RollbackTaskEntity();
		entity.setAppName(appName);
		entity.setGid(gid);
		entity.setTime(time);
		entity.setCreateTime(System.currentTimeMillis());
		entity.setRollbackStatus(RollbackStatus.INIT);
		log.info("start trans "+ appName+ " for "+gid+":"+time );
		rollbackTaskMapper.insert(entity);



	}

	/**
	 *  commit()
	 * @param gid
	 * @param time
	 * @param rollback
	 */
	public void addRollbackBySuccessCommit(String gid,long time,String appName, SagaTransaction rollback){
		updateRollbackTask(gid,time,appName,BusinessStatus.Success,rollback);
		log.info("commit trans "+ appName+ " for "+gid+":"+time );
	}

	/**
	 * rollback()，同一个saga事务会有多个rollback。但判断如果当前rollback的time是最早的一条，则表示可以回滚整个rollback
	 * 否则，只是记录，并不回滚
	 * @param gid
	 * @param time
	 * @param rollback
	 */
	public void addRollbackAfterException(String gid,long time, String appName,SagaTransaction rollback){
		updateRollbackTask(gid,time,appName,BusinessStatus.Error,rollback);
		log.info("rollback trans "+ appName+ " for "+gid+":"+time );
		int earlierCount= rollbackTaskMapper.findEarlierTransaction(gid,time);
		if(earlierCount>0){
			return ;
		}
		//最后一个回滚，需要开始回滚了
		List<RollbackTaskEntity> list = rollbackTaskMapper.allRollbackTask(gid);

		RollbackEntity rollbackEntity = rollbackMapper.single(gid);
		rollbackEntity.setTotal(list.size());
		rollbackMapper.updateById(rollbackEntity);
		log.info("rollback task total "+ list.size()+" for "+gid );

		list.stream().forEach(rollbackTaskEntity -> {
			doRollback(rollbackTaskEntity);
		});

	}

	public void doRollback(RollbackTaskEntity rollbackTaskEntity){
		String appKafka =clientTopicPrefix+"-"+rollbackTaskEntity.getAppName();
		rollbackTaskEntity.setRollbackStatus(RollbackStatus.Ongoing);
		rollbackTaskMapper.updateById(rollbackTaskEntity);
		kafkaTemplate.send(appKafka,rollbackTaskEntity.getTaskInfo());
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
		RollbackTaskEntity entity = rollbackTaskMapper.templateOne(template);
		if(success){
			entity.setRollbackStatus(RollbackStatus.Success);
			rollbackMapper.addSuccess(gid);
			//是否所有回滚都完成
			RollbackEntity  rollbackEntity = rollbackMapper.unique(gid);
			if(rollbackEntity.getTotal().equals(rollbackEntity.getSuccess())){
				//成功,目前版本暂时不做处理。可通知发起方firstAppName，或者通知所有参与放
				log.info("rollback all task for "+ appName +" for "+gid+" success ");
				rollbackEntity.setRollbackStatus(RollbackStatus.Success);
				rollbackMapper.updateById(rollbackEntity);
			}
		}else{
			entity.setRollbackStatus(RollbackStatus.Error);
		}
		entity.setUpdateTime(System.currentTimeMillis());
		rollbackTaskMapper.updateById(entity);
		log.info("rollback result for "+ appName +" for "+gid+":"+time+"  "+success );
		return  ;
	}

	protected  RollbackTaskEntity updateRollbackTask(String gid,long time, String appName,BusinessStatus businessStatus,SagaTransaction rollback){
		RollbackTaskEntity template = new RollbackTaskEntity();
		template.setTime(time);
		template.setAppName(appName);
		template.setGid(gid);
		RollbackTaskEntity entity = rollbackTaskMapper.templateOne(template);

		entity.setTaskInfo(rollback);
		entity.setStatus(businessStatus);
		entity.setUpdateTime(System.currentTimeMillis());
		rollbackTaskMapper.updateById(entity);
		return entity;
	}
}
