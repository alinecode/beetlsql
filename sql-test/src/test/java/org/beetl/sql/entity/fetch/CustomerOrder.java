package org.beetl.sql.entity.fetch;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetech.annotation.Fetch;
import org.beetl.sql.fetech.annotation.FetchOne;

import java.util.Date;

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

}
