package org.beetl.sql.ext.spring4;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.beetl.sql.core.annotatoin.AutoID;

import com.wing321.annotations.runtime.persistence.Comment;

@Table(name = "USER_ENTITY")
@Comment("用户信息")
public class UserEntity {
    @Id
    @Column(name="ID", nullable=true, scale = 10)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    @Comment("用户编号")
    private Integer id ;
    @Column(name="AGE", nullable=true)
    @Comment("年龄")
    private Integer age ;
    //用户角色
    @Column(name="ROLE_ID", nullable=true)
    @Comment("用户角色")
    private Integer roleId ;
    @Column(name="NAME", nullable=true, scale = 20)
    @Comment("姓名")
    private String name ;
    //用户名称
    @Column(name="USER_NAME", nullable=true, scale = 20)
    @Comment("用户姓名")
    private String userName ;
    @Column(name="CREATE_DATE", nullable=true)
    @Comment("创建日期")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    //for query
    @Column(name="MIN_DATE", nullable=true)
    @Comment("最小日期")
    @Temporal(TemporalType.TIMESTAMP)
    private Date minDate;
    @Column(name="MAX_DATE", nullable=true)
    @Comment("最大日期")
    @Temporal(TemporalType.TIMESTAMP)
    private Date maxDate;

   
    @AutoID  //beetlsql 注解
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }
}
