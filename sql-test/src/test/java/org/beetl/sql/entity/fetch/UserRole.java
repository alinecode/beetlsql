package org.beetl.sql.entity.fetch;

import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="sys_user_role")
public class UserRole {
	@AutoID
	Integer id;
	Integer userId;
	Integer roleId;

}
