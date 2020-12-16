package org.beetl.sql.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.Version;

import java.util.Date;

@Data
@Table(name="sys_user")
public class User {
    @AutoID
    Integer id;
    String name;
    Integer age;
    Integer departmentId;
    Date createDate;
    @Version
    Long version;
}

