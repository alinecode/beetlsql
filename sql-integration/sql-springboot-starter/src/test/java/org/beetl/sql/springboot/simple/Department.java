package org.beetl.sql.springboot.simple;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table(name=Department.virtual_table)
public class Department {
	Integer id;
	String name;
	public static final String  virtual_table = "${toTable('department')}";
}
