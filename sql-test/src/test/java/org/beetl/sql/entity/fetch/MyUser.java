package org.beetl.sql.entity.fetch;

import lombok.Data;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany2Many;

import java.util.List;
@Data
@Fetch(level = 1)
@Table(name="sys_user")
public class MyUser {
	@AutoID
	Integer id;
	String name;
	@FetchMany2Many(tableClass = UserRole.class, fromAttr = "userId",toAttr ="roleId")
	List<Role> roles;
}
