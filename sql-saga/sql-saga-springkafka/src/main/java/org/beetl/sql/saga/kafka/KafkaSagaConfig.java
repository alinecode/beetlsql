package org.beetl.sql.saga.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.common.SagaContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

/**
 * 配置使用kafka 需要的topic，重试次数等
 * @author xiandafu
 */
@Configuration
@Data
public class KafkaSagaConfig {
	// 重试次数
	@Value("{beetlsql.sega.maxTry:5}")
	protected int maxTry;
	//重试队列
	@Value("{beetlsql.sega.kafka-topic:retrySegaTopic}")
	protected String retrySegaTopic;
	//重试也失败后的发送的队列，通常人工处理
	@Value("{beetlsql.sega.kafka-topic:failSegaTopic}")
	protected String failSegaTopic;

	@Autowired
	protected ObjectMapper objectMapper ;

	@Autowired
	protected KafkaTemplate template;

	@Bean
	public SagaContextFactory segaContextFactory(){
		return new KafkaSagaContextFactory();
	}

	/**
	 * 重试回滚
	 * @param records
	 * @throws Exception
	 */
	@KafkaListener( topics = "#{'${beetlsql.sega.kafka-topic:retrySegaTopic}'}")
	public void segaTransaction(List<ConsumerRecord<?, String>> records, Acknowledgment acknowledgment) throws Exception {
		if(records.size()!=1){
			throw new IllegalStateException("期望一次消费一条");
		}
		for(ConsumerRecord<?, String> record: records){
			String json = record.value();
			KafkaSagaTransaction kafkaSegaTransaction = objectMapper.readValue(json, KafkaSagaTransaction.class);
			KafkaSagaContext kafkaSegaContext = new KafkaSagaContext(kafkaSegaTransaction,this);
			kafkaSegaContext.rollback();

		}
		acknowledgment.acknowledge();
	}


}
