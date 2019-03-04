package org.beetl.sql.test;

import java.util.Date;
import java.util.List;

import org.beetl.sql.core.annotatoin.*;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

@Table(name="user")
//@OrmQuery({
//   @OrmCondition(target = Department.class, attr="departmentId", targetAttr = "id", type=OrmQuery.Type.ONE)
//})
public class User {
    private Integer id ;

    private String name ;
    private Integer departmentId;


    @Jackson
    private Role role;

    Department department;

    @UpdateTime
    private Date createTime;
    
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
}
