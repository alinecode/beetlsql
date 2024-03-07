package org.beetl.sql.mssql;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

import java.util.Date;

@Data
@Table(name="my_user")
public class MsSqlUser {
    @AutoID
    private Integer id;
    private String name;

}
