package org.beetl.sql.entity.fetch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany;

import java.util.List;
import java.util.Objects;

@Data
@Fetch(level = 2)
@Table(name="sys_customer")
@EqualsAndHashCode(of="id")
public class Customer {
    @AutoID
    Integer id;
    String name;
    @FetchMany("customerId")
    List<CustomerOrder> order;


}
