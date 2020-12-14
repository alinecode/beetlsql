package org.beetl.sql.saga.demo.service;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.demo.entity.OrderEntity;
import org.beetl.sql.saga.demo.entity.UserEntity;
import org.beetl.sql.saga.demo.mapper.OrderMapper;
import org.beetl.sql.saga.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	@Autowired
	UserMapper userMapper;
	@Transactional(propagation= Propagation.NEVER)
	public void update(String orderId,String userId,Integer fee){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		try{
			sagaContext.start(orderId);
			UserEntity  user  = userMapper.unique(userId);
			user.setBalance(user.getBalance()-fee);
			userMapper.updateById(user);
			sagaContext.commit();
		}catch (Exception e){
			sagaContext.rollback();
		}
	}

	@Transactional(readOnly = true)
	public Integer queryBalance(String userId){
		UserEntity  user  = userMapper.unique(userId);
		return user.getBalance();
	}
}
