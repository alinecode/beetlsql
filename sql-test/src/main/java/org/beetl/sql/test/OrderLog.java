package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="order_log")
public class OrderLog {
	@AssignID
	Integer orderId;
	@AssignID
	String status;
	@Auto
	Integer id;
}
