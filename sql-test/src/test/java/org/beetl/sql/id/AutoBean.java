package org.beetl.sql.id;

import lombok.Data;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="auto_bean")
@Data
public class AutoBean {
	@AutoID
	Integer orderId;
	@Auto
	Integer age;

	String name;
}
