package org.beetl.sql.springboot.dynamic;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.saga.ms.client.KafkaLevel2Config;
import org.beetl.sql.saga.ms.client.KafkaLevel2Context;
import org.beetl.sql.saga.ms.client.KafkaLevel2Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * 模拟回滚失败后最后的处理，比如入库等待手工处理
 */
@Configuration
@Slf4j
@ImportAutoConfiguration(KafkaLevel2Config.class)
public class FailSagaConfig {


	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	KafkaLevel2Config kafkaLevel2Config;


	/**
	 * 重试回滚
	 * @param record
	 * @throws Exception
	 */
	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.retry-topic}'}")
	public void retry(ConsumerRecord<?, KafkaLevel2Transaction> record) throws Exception {
		try{
			KafkaLevel2Transaction kafkaSegaTransaction = record.value();
			KafkaLevel2Context kafkaSegaContext = new KafkaLevel2Context(kafkaSegaTransaction, kafkaLevel2Config);
			kafkaSegaContext.rollback();
		}catch(Exception ex){
			log.info(ex.getMessage());
		}

	}

//	/**
//	 * 模拟一种等待策略,这里仅仅是为了单元测试能通过
//	 * @param record
//	 * @throws Exception
//	 */
//	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.retry-topic}'}")
//	public void waitDb(ConsumerRecord<?, KafkaSagaTransaction> record) throws Exception {
//		try{
//			KafkaSagaTransaction kafkaSegaTransaction = record.value();
//			Thread.sleep(kafkaSegaTransaction.getTotalTry()*1000*3);
//			KafkaSagaContext kafkaSegaContext = new KafkaSagaContext(kafkaSegaTransaction,kafkaSagaConfig);
//			kafkaSegaContext.rollback();
//
//		}catch(Exception ex){
//			log.info(ex.getMessage());
//		}
//
//	}

	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.fail-topic}'}")
	public void fail(ConsumerRecord<?, KafkaLevel2Transaction> record) throws Exception {
		try{
			KafkaLevel2Transaction kafkaSegaTransaction = record.value();
			log.error("save to db:"+objectMapper.writeValueAsString(kafkaSegaTransaction));
		}catch(Exception ex){
			log.info(ex.getMessage());
		}
	}
}
