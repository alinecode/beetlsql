package org.beetl.sql.ymtraix;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="my_user")
@Data
public class MyUser {
	@AssignID
	Integer id;
	String name;
}
