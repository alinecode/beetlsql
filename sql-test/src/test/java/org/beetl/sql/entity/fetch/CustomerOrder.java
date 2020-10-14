package org.beetl.sql.entity.fetch;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchOne;

import java.util.Date;
import java.util.Objects;

@Data
@Table(name="sys_order")
@Fetch(level =2)
public class CustomerOrder {
    @AutoID
    Integer id;
    String name;
    Integer customerId;

    @FetchOne(value="customerId")
    Customer customer;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomerOrder that = (CustomerOrder) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
