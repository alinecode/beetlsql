package org.beetl.sql.saga.demo.web;

import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.common.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@ConditionalOnProperty(value = "spring.application.name",havingValue = "demoSystem")
public class DemoController {

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	String orderAddUrl = "http://127.0.0.1:8081/order/item/{orderId}/{userId}/{fee}";
	String userBalanceUpdateUrl = "http://127.0.0.1:8082/user/fee/{orderId}/{userId}/{fee}";
	String userBalanceQueryUrl = "http://127.0.0.1:8082/user/info/{userId}";
	String userId = "xiandafu";

	@PostMapping("/buy/{gid}")
	public String buy(@PathVariable String gid) {
		RestTemplate rest = restTemplateBuilder.build();
		int fee =3;
		Map<String, Object> paras = new HashMap<>();
		paras.put("orderId", gid);
		paras.put("userId", userId);
		//用户余额只有4元，因此，如果回滚成功，用户是可以反复调用的
		paras.put("fee", fee);
		String retStr = rest.getForEntity(userBalanceQueryUrl,String.class,paras).getBody();
		Integer balance = Integer.parseInt(retStr);
		if(balance<3){
			//不应该发生，因为回滚成功
			throw new RuntimeException("余额不足 "+balance);
		}

		log.info("buy,order id " + gid+" balance "+balance);
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		try {
			sagaContext.start(gid);
			//模拟调用俩个微服务，订单和用户
			rest.postForEntity(orderAddUrl, null,String.class, paras);
			rest.postForEntity(userBalanceUpdateUrl, null,String.class, paras);
			if (1 == 1) {
				throw new RuntimeException("模拟失败,查询saga-server 看效果");
			}
		} catch (Exception e) {
			log.info("error " + e.getMessage(),e);
			log.info("start rollback  " + e.getMessage());
			sagaContext.rollback();
			return e.getMessage();
		}

		return "SUCCESS";
	}
}
