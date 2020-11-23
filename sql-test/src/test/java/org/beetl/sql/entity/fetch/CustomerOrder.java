package org.beetl.sql.entity.fetch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchOne;

@Data
@Table(name="sys_order")
@Fetch(level =2)
@EqualsAndHashCode(of="id")
public class CustomerOrder {
    @AutoID
    Integer id;
    String name;
    Integer customerId;

    @FetchOne(value="customerId")
    Customer customer;


}
