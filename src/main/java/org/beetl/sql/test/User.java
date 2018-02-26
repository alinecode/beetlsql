package org.beetl.sql.test;

import java.util.List;

import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;
@OrmQuery(
value={
    @OrmCondition(target=Department.class,attr="departmentId",targetAttr="id",type=OrmQuery.Type.ONE),
}
)
public class User {
	private Integer id ;
	private String name ;
	private Integer departmentId;
	private Department department;
	private List<Role> myRoles;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Role> getMyRoles() {
		return myRoles;
	}
	public void setMyRoles(List<Role> myRoles) {
		this.myRoles = myRoles;
	}
	public Integer getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	

	


}
