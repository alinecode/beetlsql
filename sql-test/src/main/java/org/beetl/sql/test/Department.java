package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany;

import java.util.List;

@Data
@Table(name="department")
@Fetch
public class Department {
	private Integer id;
	private String name;
	@FetchMany("departmentId")
	private List<MyUser> list;
}
