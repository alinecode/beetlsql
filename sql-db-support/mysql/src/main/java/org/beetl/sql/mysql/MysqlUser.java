package org.beetl.sql.mysql;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

import java.util.Date;

@Data
@Table(name="user2")
public class MysqlUser {
    @AutoID
    private Integer id;
    private String name;
    private Long createTs;
    private Date day;
}
