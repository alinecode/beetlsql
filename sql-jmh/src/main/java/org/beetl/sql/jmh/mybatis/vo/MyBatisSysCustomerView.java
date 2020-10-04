package org.beetl.sql.jmh.mybatis.vo;

import lombok.Data;
import java.util.List;

@Data
public class MyBatisSysCustomerView {
    private Integer id;
    private String code;
    private String name;
    private List<MyBatisSysOrder> order;
}
