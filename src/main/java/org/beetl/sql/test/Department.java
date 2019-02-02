package org.beetl.sql.test;
import java.util.List;

import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

@OrmQuery({
   @OrmCondition(target = User.class, attr="id", targetAttr = "departmentId1", lazy=true,type=OrmQuery.Type.MANY)
})
public class Department extends TailBean {
	private Integer id ;
	private String name ;
	private List<User> user ;
	
	public Integer getId(){
		return  id;
	}
	public void setId(Integer id ){
		this.id = id;
	}
	
	public String getName(){
		return  name;
	}
	public void setName(String name ){
		this.name = name;
	}
    public List<User> getUser() {
        return user;
    }
    public void setUser(List<User> user) {
        this.user = user;
    }
  


}
