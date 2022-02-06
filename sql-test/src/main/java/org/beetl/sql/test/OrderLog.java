package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.builder.Date2Long;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.Version;

import java.time.OffsetDateTime;
import java.util.Date;

@Data
@Table(name="order_log")
public class OrderLog {
	@AssignID
	Integer orderId;
	Integer age;
	@Version
	Integer version;

	@Date2Long
	Date createTime;


}
