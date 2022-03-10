package org.beetl.sql.springboot.threadlocal;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.TargetSQLManager;

@Table(name="sys_user")
@TargetSQLManager("sqlManager2")
@Data
public class UserInfo {
    @AssignID
    private Integer id;
    private String name;

}
