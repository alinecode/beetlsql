package org.beetl.sql.oceanbase;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="my_user")
public class OceanBaseUser {
    @AutoID
    private Integer id;
    private String name;

}
