package org.beetl.sql.test.vo;


import lombok.Data;
import org.beetl.sql.annotation.entity.*;

@Data
@Table(name="user")
public class MyUser {
    static interface Simple{}

    @AssignID
    @View(Simple.class)
    private Integer id;

    @View(Simple.class)
    private String name;

    private Integer departmentId;


}
