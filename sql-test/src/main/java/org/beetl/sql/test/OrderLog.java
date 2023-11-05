package org.beetl.sql.test;

import lombok.Data;
import lombok.experimental.Accessors;
import org.beetl.sql.annotation.builder.Date2Long;
import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.core.mapping.join.AutoJsonMapper;

import java.time.OffsetDateTime;
import java.util.Date;


@Table(name="order_log")
@Data()

//@ResultProvider(AutoJsonMapper.class)
public class OrderLog {
	@AutoID
	Integer orderId;
	@Auto
	Integer age;

	String name;

}
