/**
 * 
 */
package org.beetl.sql.select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.User;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxinjie
 *
 */
public class SelectBySqlIdTest {

	private SQLLoader loader;
	private SQLManager manager;
	
	@Before
	public void before(){
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}

	/**
	 * 如果条件不匹配，默认取select * from user where 1=1的第一条记录
	 * 
	 * selectUser
		===
	    select * from user where 1=1
	    @if(user.age==1){
	    and age = #user.age#
	    @}
	 */
	@Test
	public void selectBySqlId() {
		
		User user = new User();
		user.setAge(11);
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("user", user);
		List<User> result = manager.select("user.selectUser", User.class, paras);
		System.out.println(result.get(0));
	}
	
	/**
	 * 推荐写法
	 * 
	 * 如果条件不匹配，默认取select * from user where 1=1的第一条记录
	 * 
	 * selectUser2
		===
	    select * from user where 1=1
	    @if(age==12){
	    and age = #age#
	    @}
	 */
	@Test
	public void selectBySqlId2() {
		
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("age", 12);
		List<User> result = manager.select("user.selectUser2", User.class, paras);
		System.out.println(result);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void selectBySqlId3() {
		
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("age", 12);
		List<Map> result = manager.select("user.selectUser2", Map.class, paras);
		System.out.println(result);
	}
	
}
