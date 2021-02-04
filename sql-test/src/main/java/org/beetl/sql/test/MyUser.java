package org.beetl.sql.test;


import lombok.Data;
import org.beetl.sql.annotation.builder.UpdateTime;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchOne;

import java.util.Date;

@Data
@Table(name="sys_user")
@Fetch
public class MyUser {
    @Auto()
    private Integer id;
    private String name;
    @UpdateTime
    private Date createTime;
    private Integer personId;

}
