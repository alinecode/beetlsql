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
	/*对于翻页来说，必须的一个字段，类似其他数据库的rowId，只不过需要开发来维护*/
	Integer seq;
	Integer itemCount;
	Date lastUpdateTimestamp;


}
