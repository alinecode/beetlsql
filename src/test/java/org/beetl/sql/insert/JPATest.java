package org.beetl.sql.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.JPANameConversion;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.pojo.Role;
import org.junit.Before;
import org.junit.Test;

public class JPATest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource(),new  JPANameConversion(),
				new Interceptor[]{new DebugInterceptor()});	}

	
//	public void addUser
	@Test
	public void addJPA() {

//		List<JpaData> list =  manager.all(JpaData.class);
//		System.out.println(list.size());
//		
		JpaData data = new JpaData();
		data.setName("ac");
		manager.insert(data);
		
	}
	
	
	
	@Table(name="jpa_test")
	public static class JpaData{
		@Column(name="id")
		Long id;
		@Column(name="name")
		String name;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}
