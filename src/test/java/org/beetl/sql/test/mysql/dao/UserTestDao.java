package org.beetl.sql.test.mysql.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.RowSize;
import org.beetl.sql.core.annotatoin.RowStart;
import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.annotatoin.SqlStatementType;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.test.mysql.entity.User;
/**
 * mapper方法测试
 * @author xiandafu
 *
 */
@SqlResource("userTest")
public interface UserTestDao extends BaseMapper<User>{
	/*select查询*/
	public List<User> query(User user);
	
	public List<User> query(String name);
	
	@SqlStatement(type=SqlStatementType.SELECT)
	public List<User> query2(@Param("_root") User user);
	
	@SqlStatement(type=SqlStatementType.SELECT)
	public List<User> query2(@Param("name") String name);
	
	@SqlStatement(type=SqlStatementType.SELECT,params="name")
	public List<User> query3(String name);
	
	public List<User> query(String name,@RowStart int start, @RowSize int size);
	
	@SqlStatement(type=SqlStatementType.SELECT)
	public List<User> query2(@RowStart int start, @RowSize int size,String name);
	
	@SqlStatement(type=SqlStatementType.SELECT,params="name,_st,_sz")
	public List<User> query3(String name,int start,int size);
	
	// 翻页查询
	
	public PageQuery selectUser(PageQuery query);
	public void selectUser(PageQuery query,String name);
	public PageQuery selectUser(int pageNumber ,int pageSize,String name);
	
	@SqlStatement(type=SqlStatementType.SELECT)
	public void selectUser2(PageQuery query,@Param("name") String name);
	
	//jdbc sql 直接翻页查询
	@Sql("select * from user where name like ?")
	public PageQuery jdbcQuery(int pageNumber ,int pageSize,String name);
	
	@Sql("select * from user where name like ?")
	public PageQuery jdbcQuery2(int pageNumber ,int pageSize,@Param("name") String name);
	
	//insert 
	public KeyHolder insertUser(User user);
	public KeyHolder insertUser(String name);
	
	public KeyHolder insertUser(@Param("name") String name,@Param("departmentId") Integer departmentId);
	
	@Sql("insert into user (name) value (?)")
	public void insertUser2(String  name);
	
	//update
	public void updateUser(User user);
	public void updateUser(String name,Integer id);
	public void updateUser(User user,Date createTime);
	
	@SqlStatement(type=SqlStatementType.UPDATE,params="name,id")
	public void updateUser2(String name,Integer id);
	
	@SqlStatement(type=SqlStatementType.UPDATE)
	public void updateUser3(@Param("name") String name,@Param("id") Integer id);
	
	@Sql(value="select id,name from user ")
	List<Map<String,Object>> getIdNames();
	
	@Sql(value="select id from user")
	List<Long> getIds2();
	
	@Sql(value="select id,name from user where id=?")
	Map<String,Object> getUserInfo(Long id);
	
}
