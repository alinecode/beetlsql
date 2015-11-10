/**  
 * 文件名:    User.java  
 * 描述:      
 * 作者:      suxj
 * 版本:      1.0  
 * 创建时间:  2015年8月12日 下午3:01:55  
 *  
 * 修改历史:  
 * 日期                          作者           版本         描述  
 * ------------------------------------------------------------------  
 * 2015年8月12日        suxj     1.0     1.0 Version  
 */
package org.beetl.sql.pojo;

/**
 * @ClassName: User
 * @Description: TODO
 * @author: suxj
 * @date:2015年8月12日 下午3:01:55
 */
public class User {
	Integer id;
	String name;
	Integer age;
	String userName;
	Role role;
    boolean admin;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age
				+ ", userName=" + userName + ", role=" + role + ",admin="+admin+"]";
	}

}
