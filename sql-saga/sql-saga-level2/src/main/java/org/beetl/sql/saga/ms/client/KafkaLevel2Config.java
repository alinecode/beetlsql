package org.beetl.sql.saga.ms.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.common.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;

/**
 * 配置使用kafka 需要的topic，重试次数等
 * @author xiandafu
 */
@Configuration
@Data
@Slf4j
public class KafkaLevel2Config {
	// 重试次数
	@Value("${beetlsql-saga.max-try:2}")
	protected int maxTry;
	//重试队列
	@Value("${beetlsql-saga.kafka.retry-topic:retrySagaTopic}")
	protected String retrySegaTopic;
	//重试也失败后的发送的队列，通常人工处理
	@Value("${beetlsql-saga.kafka.fail-topic:failSagaTopic}")
	protected String failSegaTopic;

	/**
	 * 事务回滚成功后的通知
	 */
	@Value("${beetlsql-saga.kafka.success-topic}")
	protected String successSegaTopic;

	@Autowired
	protected KafkaTemplate template;


	@PostConstruct
	public  void initSaga() {
		//必须设置事务实现方式
		SagaContext.sagaContextFactory = new KafkaLevel2ContextFactory(this);

	}


}
