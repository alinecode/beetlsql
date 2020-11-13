package org.beetlsql.sql.saga.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="stock")
public class Stock {
	@AssignID
	String id;
	int count;
}
