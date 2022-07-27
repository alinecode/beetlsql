package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

@Table(name="user_view",isView = true)
@Data
public class UserView {
	@Column("id")
	@AssignID
	String id;
	@Column("u_name")
	String name;
}
