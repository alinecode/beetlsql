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
//@Accessors(chain = false)

//@ResultProvider(AutoJsonMapper.class)
public class OrderLog {
	@AutoID
	Integer orderId;
	@Auto
	Integer age;

	byte[] bs;

	@Version
	Integer version;

	@Date2Long
	Date CreateTime;

	String Status;

	@Column("a_bc")
	String aBc;
	//	String ABc;

	String name;

}

