package org.beetl.sql.saga.demo.web;

import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.saga.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 模拟用户微服务
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserApi {
	@Autowired
	UserService userService;

	@PostMapping("/fee/{orderId}/{userId}/{fee}")
	public void updateBalance(@PathVariable String orderId, @PathVariable String userId, @PathVariable Integer fee) {
		log.info("update user balance "+orderId+" fee "+fee);
		userService.update(orderId, userId, fee);
	}

	@GetMapping("/info/{userId}")
	public String info( @PathVariable String userId) {
		int balance = userService.queryBalance(userId);
		log.info("query user balance "+balance);
		return String.valueOf(balance);
	}
}
