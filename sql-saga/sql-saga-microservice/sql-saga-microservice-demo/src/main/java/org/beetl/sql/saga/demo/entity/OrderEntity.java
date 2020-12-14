package org.beetl.sql.saga.demo.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="t_order")
public class OrderEntity {
	@AssignID("uuid")
	String id;
	String userId;
	String productId;
	Integer fee;
}
