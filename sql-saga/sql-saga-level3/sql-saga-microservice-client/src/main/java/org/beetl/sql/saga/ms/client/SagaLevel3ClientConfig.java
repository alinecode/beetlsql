package org.beetl.sql.saga.ms.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.ms.client.task.Server2ClientRollbackTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 配置使用kafka 需要的topic，重试次数等.此类同时也负责接收回滚任务，执行回滚实际操作
 * @author xiandafu
 */
@Configuration("sagaLevel3Config")
@Data
@Slf4j
public class SagaLevel3ClientConfig {

	/**
	 * saga-server topic
	 */
	@Value("${beetlsql-saga.kafka.server-topic:saga-server-topic}")
	protected String serverTopic;


	@Value("${beetlsql-saga.kafka.client-topic-prefix}")
	protected String appTopic;
	@Autowired
	protected KafkaTemplate template;

	@Value("${spring.application.name}")
	protected String appName;

	@Autowired
	protected ObjectMapper objectMapper;


	@PostConstruct
	public void initSaga() {
		//必须设置事务实现方式
		SagaContext.sagaContextFactory = new SagaLevel3ContextFactory(this);

	}

	/**
	 * 每个client监听topic的规则是serverTopic+'-client-'+appName
	 * @param records
	 * @throws Exception
	 */
	@KafkaListener(topics = "#{'${beetlsql-saga.kafka.client-topic-prefix}'+'-'+'${spring.application.name}'}")
	public void retry(List<ConsumerRecord<?, String>> records) throws Exception {
		for(ConsumerRecord<?, String> record:records){
			try {
				String json = record.value();
				log.info("retry rollback " + json);
				Server2ClientRollbackTask task = objectMapper.readValue(json, Server2ClientRollbackTask.class);
				SagaLevel3Transaction transaction = objectMapper.readValue(task.getTaskInfo(), SagaLevel3Transaction.class);
				SagaLevel3Context kafkaSegaContext = new SagaLevel3Context(transaction, this);
				kafkaSegaContext.setGid(task.getGid()).setTime(task.getTime());
				kafkaSegaContext.realRollback();

			} catch (Exception ex) {
				log.info(ex.getMessage(),ex);
			}
		}


	}


}
