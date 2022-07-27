package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.util.Date;

@Table(name="user")
@Data
public class QuickUser {
	@AssignID
	Integer id;
	String name ;
	@Column("create_date")
	Date date;
}
