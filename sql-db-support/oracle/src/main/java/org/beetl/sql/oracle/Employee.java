package org.beetl.sql.oracle;

import lombok.Data;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;
import java.util.Date;

@Table(name="scott.emp")
@Data
public class Employee {

	@Column("empno")
	Integer empno;
	@Column("ENAME")
	String ename;


}
