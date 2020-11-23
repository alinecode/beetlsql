package org.beetl.sql.sample.entity;


import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="sys_user")
public class UserEntity {

    @AutoID
    private Integer id;
    private String name;
    private Integer departmentId;

}
