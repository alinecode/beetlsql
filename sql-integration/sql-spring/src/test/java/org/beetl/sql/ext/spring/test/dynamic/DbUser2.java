package org.beetl.sql.ext.spring.test.dynamic;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.TargetSQLManager;

@Table(name="sys_user")
@TargetSQLManager("b")
@Data
public class DbUser2 {
    @AssignID
    private Long id;
    private String name;
}
