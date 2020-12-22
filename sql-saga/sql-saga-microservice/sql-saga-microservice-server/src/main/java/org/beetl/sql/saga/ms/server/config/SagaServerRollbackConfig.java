package org.beetl.sql.saga.ms.server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.ms.client.task.*;
import org.beetl.sql.saga.ms.server.service.SagaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

/**
 * kafka 配置
 */
@Configuration
@Slf4j
public class SagaServerRollbackConfig {
	@Autowired
	SagaManager sagaManager;

	@Autowired
	ObjectMapper objectMapper;


	@KafkaListener(topics = "#{'${beetlsql-saga.kafka.server-topic}'}")
	public void client2ServerTask(List<ConsumerRecord<?, String>> records) throws Exception {

		for (ConsumerRecord<?, String> record : records) {
			String json = record.value();
			try {
				log.info(json);
				JsonNode node = objectMapper.readTree(json);
				String type = node.get("@Clazz").asText();
				Class<?> clz = null;
				clz = Class.forName(type);
				String appName = node.get("appName").asText();
				String gid = node.get("gid").asText();
				long time = node.get("time").longValue();
				if (clz == StartClientTask.class) {
					StartClientTask startTask = new StartClientTask(appName,gid, time);
					sagaManager.addStartTask(startTask.getGid(), startTask.getTime(), appName);
				} else if (clz == RollbackInCommitClientTask.class) {
					String rollbackTaskJson = node.get("rollback").toString();
					sagaManager.addRollbackBySuccessCommit(gid, time, appName, rollbackTaskJson);
				} else if (clz == RollbackClientTask.class) {
					String rollbackTaskJson = node.get("rollback").toString();
					sagaManager.addRollbackAfterException(gid, time, appName, rollbackTaskJson);
				} else if (clz == RollbackFailureClientTask.class) {
					String rollbackTaskJson = node.get("rollback").toString();
					sagaManager.notifyRollback(gid, time, false, appName,rollbackTaskJson);
				} else if (clz == RollbackSuccessClientTask.class) {
					sagaManager.notifyRollback(gid, time, true, appName,null);
				} else {
					throw new IllegalStateException("error task " + clz.getClass());
				}
			} catch (RuntimeException re) {
				log.info(re.getMessage(), re);
				//saga server自身的任何错误（如数据库问题）通过消息机制重试时重试
				throw re;

			}
		}

	}



}
