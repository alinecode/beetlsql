package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="User")
@Data
public class User {
	String name ;
	@AssignID
	Integer id;
	String aBc;

}
