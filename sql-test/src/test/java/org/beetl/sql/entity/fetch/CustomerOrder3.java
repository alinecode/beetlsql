package org.beetl.sql.entity.fetch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchOne;

@Data
@Table(name="sys_order")
@Fetch()
@EqualsAndHashCode(of="id")
public class CustomerOrder3 {
    @AutoID
    Integer id;
    String name;
    Integer customerId;
	//如果sql模板执行上下文中包含了"c"，参考DynamicFetchEnableOnFunction
    @FetchOne(value="customerId",enableOn = "c")
    Customer customer;

}
