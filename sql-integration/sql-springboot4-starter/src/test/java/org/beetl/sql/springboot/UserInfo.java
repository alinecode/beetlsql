package org.beetl.sql.springboot;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="sys_user")
@Data
public class UserInfo {
    @AssignID
    private Long id;
    private String name;
}
