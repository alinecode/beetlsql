package org.beetl.sql.saga.demo.web;

import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.demo.service.OrderService;
import org.beetl.sql.saga.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟订单微服务
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderApi {
	@Autowired
	OrderService orderService;
	@RequestMapping("/item/{orderId}/{userId}/{fee}")
	public void updateBalance(@PathVariable String orderId,@PathVariable String userId ,@PathVariable Integer fee){
		log.info("add order item "+orderId+" fee "+fee);
		orderService.addOrder(orderId,userId,fee);
	}
}
