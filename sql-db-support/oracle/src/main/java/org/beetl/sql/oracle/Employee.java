package org.beetl.sql.oracle;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;

import java.sql.Timestamp;
import java.util.Date;

@Table(name="hr.employees")
@Data
public class Employee {
	Integer employeeId;
	String firstName;
	Date hireDate;

}
