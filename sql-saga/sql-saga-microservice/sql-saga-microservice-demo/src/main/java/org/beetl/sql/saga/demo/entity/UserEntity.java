package org.beetl.sql.saga.demo.entity;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name="t_user")
public class UserEntity {
	@AssignID("uuid")
	String id;
	String name;
	Integer balance;
}
