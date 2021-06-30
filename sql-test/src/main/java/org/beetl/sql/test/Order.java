package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="order")
public class Order {
	Integer userId;
	String userName;
	@AutoID
	Integer id;
}
