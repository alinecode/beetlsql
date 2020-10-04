package org.beetl.sql.kingbase;


import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;

@Table(name="user_info")
@Data
public class UserInfo {
    @AssignID
    Integer id;
    String name;
    Timestamp createDate;
}
