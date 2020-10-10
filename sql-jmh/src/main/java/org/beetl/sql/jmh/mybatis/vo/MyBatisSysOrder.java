package org.beetl.sql.jmh.mybatis.vo;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;


@Data
public class MyBatisSysOrder {
    private Integer id;
    private String name;
    private Integer customerId;
}
