package org.beetl.sql.saga.ms.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.ms.client.task.Server2ClientRollbackTask;
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

	@Value("${beetlsql-saga.kafka.client-topic-prefix}")
	String clientTopicPrefix;

	@Autowired
	KafkaTemplate kafkaTemplate;

	/**
	 * start()
	 * @param gid
	 * @param time
	 * @param appName
	 */
	public void addStartTask(String gid, long time, String appName) {

		RollbackEntity rollbackEntity = rollbackMapper.single(gid);
		if (rollbackEntity == null) {
			rollbackEntity = new RollbackEntity();
			rollbackEntity.setGid(gid);
			rollbackEntity.setFirstAppName(appName);
			rollbackEntity.setCreateTime(System.currentTimeMillis());
			rollbackEntity.setTotal(null);
			rollbackEntity.setRollbackStatus(RollbackStatus.INIT);
			rollbackEntity.setSuccess(0);
			rollbackMapper.insert(rollbackEntity);
			log.info("start gid trans for " + gid + " from " + appName);
		}
		RollbackTaskEntity entity = new RollbackTaskEntity();
		entity.setAppName(appName);
		entity.setGid(gid);
		entity.setTime(time);
		entity.setCreateTime(System.currentTimeMillis());
		entity.setRollbackStatus(RollbackStatus.INIT);

		log.info("start trans " + appName + " for " + gid + ":" + time);
		rollbackTaskMapper.insert(entity);


	}

	/**
	 *  commit()
	 * @param gid
	 * @param time
	 * @param rollback
	 */
	public void addRollbackBySuccessCommit(String gid, long time, String appName, String rollback) {
		RollbackTaskEntity rollbackTaskEntity = updateRollbackTask(gid, time, appName, BusinessStatus.Success, rollback);
		if(rollbackTaskEntity==null){
			return ;
		}
		log.info("commit trans " + appName + " for " + gid + ":" + time);
		int earlierCount = rollbackTaskMapper.findEarlierTransaction(gid, time);
		if (earlierCount > 0) {
			return;
		}
		//全部都成功了，考虑删除
		removeGid(gid);
		notifyClient4CommitSuccess(gid);

	}

	/**
	 * rollback()，同一个saga事务会有多个rollback。但判断如果当前rollback的time是最早的一条，则表示可以回滚整个rollback
	 * 否则，只是记录，并不回滚
	 * @param gid
	 * @param time
	 * @param rollbackJson
	 */
	public void addRollbackAfterException(String gid, long time, String appName, String rollbackJson) {
		RollbackTaskEntity dbRollbackTaskEntity = updateRollbackTask(gid, time, appName, BusinessStatus.Error, rollbackJson);
		if(dbRollbackTaskEntity==null){
			return ;
		}
		log.info("rollback trans " + appName + " for " + gid + ":" + time);
		int earlierCount = rollbackTaskMapper.findEarlierTransaction(gid, time);
		if (earlierCount > 0) {
			return;
		}
		//最后一个回滚，需要开始回滚了
		List<RollbackTaskEntity> list = rollbackTaskMapper.allRollbackTask(gid);

		RollbackEntity rollbackEntity = rollbackMapper.single(gid);
		rollbackEntity.setTotal(list.size());
		rollbackMapper.updateById(rollbackEntity);
		log.info("rollback task total " + list.size() + " for " + gid);

		list.stream().forEach(rollbackTaskEntity -> {
			doRollback(rollbackTaskEntity);
		});

	}

	public void doRollback(RollbackTaskEntity rollbackTaskEntity) {
		log.info("do rollback " + rollbackTaskEntity);
		String appKafka = clientTopicPrefix + "-" + rollbackTaskEntity.getAppName();
		rollbackTaskEntity.setRollbackStatus(RollbackStatus.Ongoing);
		rollbackTaskMapper.updateById(rollbackTaskEntity);

		Server2ClientRollbackTask server2ClientRollbackTask = new Server2ClientRollbackTask();
		server2ClientRollbackTask.setGid(rollbackTaskEntity.getGid());
		server2ClientRollbackTask.setTime(rollbackTaskEntity.getTime());
		server2ClientRollbackTask.setServerTaskId(rollbackTaskEntity.getId());
		server2ClientRollbackTask.setTaskInfo(rollbackTaskEntity.getTaskInfo());

		try {
			kafkaTemplate.send(appKafka, rollbackTaskEntity.getGid(),objectMapper.writeValueAsString(server2ClientRollbackTask));
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}


	/**
	 * 微服务通知saga-server 回滚是否成功。
	 * @param gid
	 * @param time
	 * @param success
	 */
	public void notifyRollback(String gid, long time, boolean success, String appName,String rollbackTaskJson) {
		RollbackTaskEntity template = new RollbackTaskEntity();
		template.setTime(time);
		template.setAppName(appName);
		template.setGid(gid);
		RollbackTaskEntity entity = rollbackTaskMapper.templateOne(template);
		if(entity==null){
			log.error("找不到回滚任务 "+gid+":"+time);
			//忽略,不可能发生
			return ;
		}
		if (success) {
			entity.setRollbackStatus(RollbackStatus.Success);
			rollbackMapper.addSuccess(gid);
			//是否所有回滚都完成
			RollbackEntity rollbackEntity = rollbackMapper.unique(gid);
			if (rollbackEntity.getTotal().equals(rollbackEntity.getSuccess())) {
				//所有回滚成功,目前版本暂时不做处理。可通知发起方firstAppName，或者通知所有参与方
				//不立即掉用removeGid,可以手工清理或者延迟清理
				log.info("rollback all task for " + appName + " for " + gid + " success ");
				rollbackEntity.setRollbackStatus(RollbackStatus.Success);
				rollbackMapper.updateById(rollbackEntity);
				notifyClient4RollbackSuccess(gid);


			}
		} else {
			entity.setRollbackStatus(RollbackStatus.Error);
			entity.setTaskInfo(rollbackTaskJson);
		}
		entity.setUpdateTime(System.currentTimeMillis());
		rollbackTaskMapper.updateById(entity);
		log.info("rollback result for " + appName + " for " + gid + ":" + time + "  " + success);
		return;
	}

	protected RollbackTaskEntity updateRollbackTask(String gid, long time, String appName,
			BusinessStatus businessStatus, String rollbackJson) {
		RollbackTaskEntity template = new RollbackTaskEntity();
		template.setTime(time);
		template.setAppName(appName);
		template.setGid(gid);
		RollbackTaskEntity entity = rollbackTaskMapper.templateOne(template);
		if (entity == null) {
			log.error("未能发现回滚任务 " + gid + ":" + time);
			return null;
		}
		entity.setTaskInfo(rollbackJson);
		entity.setStatus(businessStatus);
		entity.setUpdateTime(System.currentTimeMillis());
		rollbackTaskMapper.updateById(entity);
		return entity;
	}

	protected  void removeGid(String gid){
		rollbackMapper.deleteById(gid);
		rollbackTaskMapper.removeRollbackTask(gid);
	}

	/**
	 * 全部事务成功提交后通知
	 * @param gid
	 */
	protected void notifyClient4CommitSuccess(String gid){
		/**
		 * 成当saga事务成功后，是否需要通知所有app，考虑到通信代价，以及并非所有项目都有这需求，目前版本不打算完成
		 * 实际需求是有的，但可以通过业务编程来解决。比如微服务最后一个环节结束后，通知某些微服务
		 */

	}

	/**
	 * 全部事务回滚成功后通知
	 * @param gid
	 */
	protected void notifyClient4RollbackSuccess(String gid){
		/**
		 * 目前不考虑通知客户端所有回滚成功
		 */
	}
}
