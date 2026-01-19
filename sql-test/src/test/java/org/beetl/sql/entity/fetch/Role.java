package org.beetl.sql.entity.fetch;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany2Many;

import java.util.List;

@Table(name = "sys_role")
@Data
@Fetch(level = 1)
public class Role {
	@AutoID
	Integer id;
	String name;
	@FetchMany2Many(tableClass = UserRole.class, fromAttr = "roleId",toAttr = "userId")
	List<MyUser> users;
}
