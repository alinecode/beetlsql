package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="my_test",assignId = true)
@Data
public class MyTestEntity {
	@AssignID
	Integer id;
	Integer id2;
	String name;
}
