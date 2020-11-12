package org.beetl.sql.sega.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.sega.common.SegaContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

/**
 * 配置使用kafka 需要的topic，重试次数等
 * @author xiandafu
 */
@Configuration
@Data
public class KafkaSegaConfig {
	@Value("{beetlsql.sega.maxTry:5}")
	private int maxTry;
	@Value("{beetlsql.sega.kafka-topic:retrySegaTopic}")
	private String retrySegaTopic;

	@Autowired
	ObjectMapper objectMapper ;

	@Autowired
	KafkaTemplate template;
	@Bean
	public SegaContextFactory  segaContextFactory(){
		return new KafkaSegaContextFactory();
	}

	/**
	 * 重试回滚
	 * @param records
	 * @throws Exception
	 */
	@KafkaListener( topics = "#{'${beetlsql.sega.kafka-topic:retrySegaTopic}'}")
	public void segaTransaction(List<ConsumerRecord<?, String>> records) throws Exception {
		for(ConsumerRecord<?, String> record: records){
			String json = record.value();
			KafkaSegaTransaction kafkaSegaTransaction = objectMapper.readValue(json,KafkaSegaTransaction.class);
			KafkaSegaContext kafkaSegaContext = new KafkaSegaContext(kafkaSegaTransaction,this);
			kafkaSegaContext.rollback();
		}
	}


}
