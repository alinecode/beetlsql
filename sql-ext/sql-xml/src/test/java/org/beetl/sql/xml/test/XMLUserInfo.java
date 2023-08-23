package org.beetl.sql.xml.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="sys_user")
@Data
public class XMLUserInfo {
    @AssignID
    private Long id;
    private String name;
}
