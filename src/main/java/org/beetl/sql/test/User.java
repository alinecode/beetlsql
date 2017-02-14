package org.beetl.sql.test;
import java.sql.Date;

import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.orm.OrmCondition;
import org.beetl.sql.core.orm.OrmQuery;

@OrmQuery(
{
	@OrmCondition(target=Department.class,attr="departmentId",targetAttr="id",type=OrmQuery.Type.ONE,alias="myDept")
//	@OrmCondition(target=ProductOrder.class,attr="id",targetAttr="userId" ,type=OrmQuery.Type.MANY),
//	@OrmCondition(target=Role.class,attr="id",targetAttr="userId" ,sqlId="user.selectRole",type=OrmQuery.Type.MANY)

}
)
public class User   extends TailBean {
	
	private Integer id ;
	private String name ;
	private Integer departmentId;
	private Date createTime;
	private char[] content ;
	private Color color;
	
	public User() {
	}
	
	@AssignID
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

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public char[] getContent() {
		return content;
	}

	public void setContent(char[] content) {
		this.content = content;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	
	

}
