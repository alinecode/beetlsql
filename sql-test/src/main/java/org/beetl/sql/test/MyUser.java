package org.beetl.sql.test;


import lombok.Data;
import org.beetl.sql.annotation.builder.UpdateTime;
import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany;

import java.util.Date;
import java.util.List;

@Data
@Table(name="sys_user")
@Fetch
public class MyUser {
    @AssignID("uuid")
    private Integer id;
    private String name;
    @UpdateTime
    private Date createTime;
    private Integer departmentId;






}
