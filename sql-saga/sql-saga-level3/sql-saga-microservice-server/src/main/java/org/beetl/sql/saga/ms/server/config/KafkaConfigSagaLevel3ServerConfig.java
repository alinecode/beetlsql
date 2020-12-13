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
public class KafkaConfigSagaLevel3ServerConfig {
	@Autowired
	SagaManager sagaManager;

	@Autowired
	ObjectMapper objectMapper;

	public static void main(String[] args) throws Exception {
		StartClientTask startTask = new StartClientTask();
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(startTask);
		json = "{\"@Clazz\":\"org.beetl.sql.saga.ms.client.task.RollbackInCommitTask\",\"appName\":\"orderSystem\",\"gid\":\"1235\",\"time\":1105281946456485,\"rollback\":{\"tasks\":[{\"@Clazz\":\"org.beetl.sql.saga.ms.client.SagaLevel3Transaction$KafkaSagaTaskTrace\",\"rollbackTask\":{\"@Clazz\":\"org.beetl.sql.saga.common.ami.SagaInsertAMI$InsertSagaRollbackTask\",\"sqlManagerName\":\"mySqlManager\",\"entityClass\":\"org.beetl.sql.saga.demo.entity.OrderEntity\",\"pkId\":\"d9241d4a-df30-4220-b586-dda9b16924d7\"},\"success\":false}],\"id\":\"9909a58b-2823-4110-8b92-f0264ed1e927\",\"success\":true}}";
		JsonNode node = objectMapper.readTree(json);
		String rollbackTaskJson = node.get("rollback").toString();
		System.out.println(rollbackTaskJson);
	}

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
					StartClientTask startTask = new StartClientTask(gid, time);
					startTask.setAppName(appName);
					sagaManager.addStartTask(startTask.getGid(), startTask.getTime(), appName);
				} else if (clz == RollbackInCommitClientTask.class) {
					String rollbackTaskJson = node.get("rollback").toString();
					sagaManager.addRollbackBySuccessCommit(gid, time, appName, rollbackTaskJson);
				} else if (clz == RollbackClientTask.class) {
					String rollbackTaskJson = node.get("rollback").toString();
					sagaManager.addRollbackAfterException(gid, time, appName, rollbackTaskJson);
					;
				} else if (clz == RollbackFailureClientTask.class) {
					sagaManager.notifyRollback(gid, time, false, appName);
				} else if (clz == RollbackSuccessClientTask.class) {

					sagaManager.notifyRollback(gid, time, true, appName);
				} else {
					throw new IllegalStateException("error task " + clz.getClass());
				}
			} catch (RuntimeException re) {
				log.info(re.getMessage(), re);
				log.info("task ignore with error "+json);

			}
		}

	}



}
