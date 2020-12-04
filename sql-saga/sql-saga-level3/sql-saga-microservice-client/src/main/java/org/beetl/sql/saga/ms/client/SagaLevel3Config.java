package org.beetl.sql.saga.ms.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.ms.client.task.RollbackTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;

/**
 * 配置使用kafka 需要的topic，重试次数等
 * @author xiandafu
 */
@Configuration
@Data
@Slf4j
public class SagaLevel3Config {

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
	protected  String appName;


	@PostConstruct
	public  void initSaga() {
		//必须设置事务实现方式
		SagaContext.sagaContextFactory = new SagaLevel3ContextFactory(this);

	}

	/**
	 * 每个client监听topic的规则是serverTopic+'-client-'+appName
	 * @param record
	 * @throws Exception
	 */
	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.client-topic-prefix}+'-'+'${spring.application.name}'}")
	public void retry(ConsumerRecord<?, RollbackTask> record) throws Exception {
		try{
			RollbackTask task = record.value();
			SagaLevel3Context kafkaSegaContext = new SagaLevel3Context((SagaLevel3Transaction)task.getRollback(),this);
			kafkaSegaContext.realRollback();
		}catch(Exception ex){
			log.info(ex.getMessage());
		}

	}


}
