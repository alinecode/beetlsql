package org.beetl.sql.jmh.beetl.vo;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="sys_user")
@Data
public class BeetlSQLSysUser {
    @AssignID
    private Integer id ;
    private String code ;
}
