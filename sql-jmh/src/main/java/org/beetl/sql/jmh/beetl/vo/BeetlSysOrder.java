package org.beetl.sql.jmh.beetl.vo;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetech.annotation.Fetch;

@Table(name="sys_order")
@Data
@Fetch
public class BeetlSysOrder {
    @AssignID
    private Integer id;
    private String name;
    private Integer customerId;
}
