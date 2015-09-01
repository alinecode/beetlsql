package org.beetl.sql.core.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.sql.core.SQLLoader;

public class Beetl {
	
	GroupTemplate gt = null;  
	public Beetl(SQLLoader loader) {
		try {
			StringSqlTemplateLoader resourceLoader = new StringSqlTemplateLoader(loader);
			Properties ps = loadDefaultConfig();
			Properties ext = loadExtConfig();
			ps.putAll(ext);
			Configuration cfg =new Configuration(ps);			
			gt = new GroupTemplate(resourceLoader, cfg);
			
			boolean product = Boolean.parseBoolean(ps.getProperty("PRODUCT_MODE"));
			loader.setAutoCheck(!product);
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	/***
	 * 加载cfg自定义配置
	 * 
	 * @param cfg
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
				"/btsql-ext.properties");
		if(ins==null) return ps;
		try {
			ps.load(ins);
		} catch (IOException e) {
			throw new RuntimeException("默认配置文件加载错:/btsql.properties");
		}
		return ps;	
	}



	public GroupTemplate getGroupTemplate() {
		return gt;
	}
	
	
}
