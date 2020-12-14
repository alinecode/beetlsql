package org.beetl.sql.saga.demo.service;

import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.demo.entity.OrderEntity;
import org.beetl.sql.saga.demo.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
	@Autowired
	OrderMapper orderMapper;
	@Transactional(propagation=Propagation.NEVER)
	public void addOrder(String orderId,String userId,Integer fee){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		try{
			sagaContext.start(orderId);
			OrderEntity orderEntity = new OrderEntity();
			orderEntity.setFee(fee);
			orderEntity.setUserId(userId);
			orderEntity.setProductId("any");
			orderMapper.insert(orderEntity);
			sagaContext.commit();
		}catch (Exception e){
			sagaContext.rollback();
			throw new RuntimeException(e);
		}
	}
}
