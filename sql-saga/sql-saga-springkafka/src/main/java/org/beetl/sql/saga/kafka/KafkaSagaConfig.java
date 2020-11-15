package org.beetl.sql.saga.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 配置使用kafka 需要的topic，重试次数等
 * @author xiandafu
 */
@Configuration
@Data
@Slf4j
public class KafkaSagaConfig {
	// 重试次数
	@Value("${beetlsql.saga.maxTry:2}")
	protected int maxTry;
	//重试队列
	@Value("${beetlsql.saga.kafka-topic:retrySagaTopic}")
	protected String retrySegaTopic;
	//重试也失败后的发送的队列，通常人工处理
	@Value("${beetlsql.saga.kafka-topic:failSagaTopic}")
	protected String failSegaTopic;

	@Autowired
	protected KafkaTemplate template;


	@PostConstruct
	public  void initSaga() {
		//必须设置事务实现方式
		SagaContext.sagaContextFactory = new KafkaSagaContextFactory(this);

	}

	/**
	 * 重试回滚
	 * @param record
	 * @throws Exception
	 */
	@KafkaListener( topics = "#{'${beetlsql.saga.kafka-topic:retrySagaTopic}'}")
	public void segaTransaction(ConsumerRecord<?, KafkaSagaTransaction> record) throws Exception {
		try{
			KafkaSagaTransaction kafkaSegaTransaction = record.value();
			KafkaSagaContext kafkaSegaContext = new KafkaSagaContext(kafkaSegaTransaction,this);
			kafkaSegaContext.rollback();
		}catch(Exception ex){
			log.info(ex.getMessage());
		}

	}


}
