package org.beetl.sql.test;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="User")
public class User {
	String name ;
	@AssignID
	Integer id;
}
