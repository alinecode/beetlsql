package org.beetl.sql.springboot.shardjdbc;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Table(name="t_order")
@Data
public class TOrder {
	Integer orderId;
	Integer userId;
}
