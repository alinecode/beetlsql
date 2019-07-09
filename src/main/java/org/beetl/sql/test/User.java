package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.Jackson;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.annotatoin.UpdateTime;

import java.util.Date;

@Table(name="user")
//@OrmQuery({
//   @OrmCondition(target = Department.class, attr="departmentId", targetAttr = "id", type=OrmQuery.Type.ONE)
//})
public class User extends BaseEntity {


	private Integer departmentId;

    @Jackson
    private Role role;

    Department department;

    @UpdateTime
    private Date createTime;
    


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
