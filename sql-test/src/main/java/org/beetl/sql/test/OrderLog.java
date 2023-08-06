package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.builder.Date2Long;
import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;

import java.time.OffsetDateTime;
import java.util.Date;

@Data
@Table(name="order_log")
//@ResultProvider(AutoJsonMapper.class)
public class OrderLog {
	@AutoID
	Integer orderId;
	Integer age;
	@Version
	Integer version;

	@Date2Long
	Date createTime;

	String status;


}
