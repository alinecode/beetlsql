package org.beetl.sql.core.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.sql.core.SQLLoader;

public class Beetl {
	
	GroupTemplate gt = null;  
	Properties ps = null;
	public Beetl(SQLLoader loader,Properties other) {
		try {
			
			ps = loadDefaultConfig();
			Properties ext = loadExtConfig();
			ps.putAll(ext);
			ps.putAll(other);
			boolean product = Boolean.parseBoolean(ps.getProperty("PRODUCT_MODE"));
			StringSqlTemplateLoader resourceLoader = new StringSqlTemplateLoader(loader,product);
			Configuration cfg =new Configuration(ps);			
			gt = new GroupTemplate(resourceLoader, cfg);		
			loader.setAutoCheck(!product);
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	/***
	 * 加载cfg自定义配置
	 * 
	 * @return
	 */
	public Properties loadDefaultConfig () {
		Properties ps  = new Properties();
		InputStream ins = this.getClass().getResourceAsStream(
				"/btsql.properties");
		if(ins==null) return ps;
		try {
			ps.load(ins);
		} catch (IOException e) {
			throw new RuntimeException("默认配置文件加载错:/btsql.properties");
		}
		return ps;	
	}
	
	
	public Properties loadExtConfig () {
		Properties ps  = new Properties();
		InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				"btsql-ext.properties");
		if(ins==null) return ps;
		try {
			ps.load(ins);
			ins.close();
		} catch (IOException e) {
			throw new RuntimeException("默认配置文件加载错:/btsql.properties");
		}
		
		return ps;	
	}
	
	


	public GroupTemplate getGroupTemplate() {
		return gt;
	}

	public Properties getPs() {
		return ps;
	}
	
	
}
