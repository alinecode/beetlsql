package org.beetl.sql.trino;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@Table(name="orders")
public class TrinoOrder {
    @AssignID
	@Column("orderkey")
    Long orderKey;
	@Column("custkey")
    Long custKey ;
	@Column("orderstatus")
	String orderStatus;
	@Column("totalprice")
	BigDecimal totalPrice;
	@Column("orderdate")
	Date orderDate;

	String comment;
}
