package org.beetl.sql.springboot.dynamicds;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

@Data
@Table( name="department")
public class Dept {
	Integer id;
	String name;

}
