package org.beetl.sql.entity.fetch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchSql;

import java.util.List;
import java.util.Objects;

@Data
@Table(name="sys_order")
@Fetch(level =2)
@EqualsAndHashCode(of="id")
public class CustomerOrder2 {
    @AutoID
    Integer id;
    String name;
    Integer customerId;
    @FetchSql("select * from sys_customer where id =#{customerId}")
    Customer customer;
	@FetchSql("select * from sys_customer s where s.id =#{customerId} order by s.id desc")
	List<Customer> customers;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomerOrder2 that = (CustomerOrder2) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
