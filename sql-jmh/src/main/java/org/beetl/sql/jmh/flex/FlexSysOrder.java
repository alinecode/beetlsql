package org.beetl.sql.jmh.flex;

import com.mybatisflex.annotation.Table;
import lombok.Data;


@Data
@Table(value = "sys_order")
public class FlexSysOrder {
    private Integer id;
    private String name;
    private Integer customerId;
}
