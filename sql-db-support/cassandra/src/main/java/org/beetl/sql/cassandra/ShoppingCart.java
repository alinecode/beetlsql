package org.beetl.sql.cassandra;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.util.Date;

@Data
@Table(name="store.shopping_cart")
public class ShoppingCart {
	@AssignID
	@Column("userid")
	Integer userId;
	Integer seq;
	Integer itemCount;
	Date lastUpdateTimestamp;


}
