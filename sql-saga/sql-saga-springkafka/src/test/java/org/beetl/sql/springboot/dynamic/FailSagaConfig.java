package org.beetl.sql.springboot.dynamic;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.saga.kafka.KafkaSagaConfig;
import org.beetl.sql.saga.kafka.KafkaSagaContext;
import org.beetl.sql.saga.kafka.KafkaSagaTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * 模拟回滚失败后最后的处理，比如入库等待手工处理
 */
@Configuration
@Slf4j
public class FailSagaConfig {


	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	KafkaSagaConfig kafkaSagaConfig;


//	/**
//	 * 重试回滚
//	 * @param record
//	 * @throws Exception
//	 */
//	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.retry-topic}'}")
//	public void retry(ConsumerRecord<?, KafkaSagaTransaction> record) throws Exception {
//		try{
//			KafkaSagaTransaction kafkaSegaTransaction = record.value();
//			KafkaSagaContext kafkaSegaContext = new KafkaSagaContext(kafkaSegaTransaction,kafkaSagaConfig);
//			kafkaSegaContext.rollback();
//		}catch(Exception ex){
//			log.info(ex.getMessage());
//		}
//
//	}

	/**
	 * 模拟一种等待策略,这里仅仅是为了单元测试能通过
	 * @param record
	 * @throws Exception
	 */
	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.retry-topic}'}")
	public void waitDb(ConsumerRecord<?, KafkaSagaTransaction> record) throws Exception {
		try{
			KafkaSagaTransaction kafkaSegaTransaction = record.value();
			Thread.sleep(kafkaSegaTransaction.getTotalTry()*1000*3);
			KafkaSagaContext kafkaSegaContext = new KafkaSagaContext(kafkaSegaTransaction,kafkaSagaConfig);
			kafkaSegaContext.rollback();

		}catch(Exception ex){
			log.info(ex.getMessage());
		}

	}

	@KafkaListener( topics = "#{'${beetlsql-saga.kafka.fail-topic}'}")
	public void fali(ConsumerRecord<?, KafkaSagaTransaction> record) throws Exception {
		try{
			KafkaSagaTransaction kafkaSegaTransaction = record.value();
			log.error("save to db:"+objectMapper.writeValueAsString(kafkaSegaTransaction));
		}catch(Exception ex){
			log.info(ex.getMessage());
		}
	}
}
