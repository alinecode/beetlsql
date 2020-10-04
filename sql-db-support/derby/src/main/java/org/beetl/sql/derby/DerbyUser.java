package org.beetl.sql.derby;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="t_user_1")
@Data
public class DerbyUser {
    @AssignID
    Integer id;
    String name;

}
