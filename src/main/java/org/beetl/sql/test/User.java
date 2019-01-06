package org.beetl.sql.test;

import java.util.List;

import org.beetl.sql.core.annotatoin.LogicDelete;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

//@Table(name="user")
//@OrmQuery({
//   @OrmCondition(target = Department.class, attr="departmentId", targetAttr = "id", type=OrmQuery.Type.ONE)
//})
public class User {
	private Integer id ;
	private String name ;
	private Integer departmentId;
	private Department department;
	private List<Role> myRoles;
	@LogicDelete(value=1)
	private Integer delFlag;
	
	private String rType;
	
	public String getrType() {
        return rType;
    }
    public void setrType(String rType) {
        this.rType = rType;
    }
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
    public Integer getDelFlag() {
        return delFlag;
    }
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
	
	

	


}
