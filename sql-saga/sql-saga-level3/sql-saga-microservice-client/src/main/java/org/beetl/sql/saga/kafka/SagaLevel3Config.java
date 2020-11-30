package org.beetl.sql.saga.kafka;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.common.SagaContext;
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

	@Autowired
	protected KafkaTemplate template;



	@PostConstruct
	public  void initSaga() {
		//必须设置事务实现方式
		SagaContext.sagaContextFactory = new SagaLevel3ContextFactory(this);

	}


	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.rollback-topic}'}")
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
