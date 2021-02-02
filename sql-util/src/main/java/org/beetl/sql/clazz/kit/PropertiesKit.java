package org.beetl.sql.clazz.kit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesKit {

	static PropertiesKit propertiesKit = new PropertiesKit();
	Properties ps = new Properties();

	public PropertiesKit(){
		Properties defaultConfig = loadDefaultConfig();
		Properties extConfig = loadExtConfig();
		ps.putAll(defaultConfig);
		ps.putAll(extConfig);
	}

	public static  PropertiesKit  getInstance(){
		return propertiesKit;
	}

	public Properties getPs(){
		return ps;
	}

	public String getValue(String name){
		return ps.getProperty(name);
	}

	public Integer getIntValue(String name){

		return Integer.parseInt(ps.getProperty(name).trim());
	}

	public Integer getIntValue(String name,String defaultValue){

		return Integer.parseInt(ps.getProperty(name,defaultValue).trim());
	}

	/***
	 * 加载cfg自定义配置
	 *
	 * @return
	 */
	static public Properties loadDefaultConfig() {
		Properties ps = new Properties();
		InputStream ins =loadIns("/btsql.properties");
		if (ins == null) {
			throw new IllegalStateException("默认配置文件加载错:找不到 btsql.properties");
		}
		try {
			ps.load(ins);
		} catch (IOException e) {
			throw new IllegalStateException("默认配置文件加载错:btsql.properties");
		}
		return ps;
	}


	private static Properties loadExtConfig() {
		Properties ps = new Properties();
		InputStream ins = loadIns("btsql-ext.properties");
		if (ins == null) {
			return ps;
		}

		try {
			ps.load(ins);
			ins.close();
		} catch (IOException e) {
			throw new IllegalStateException("默认配置文件加载错:/btsql-ext.properties");
		}

		return ps;
	}

	protected  static InputStream loadIns(String resource){
		if(Thread.currentThread().getContextClassLoader()!=null){
			InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
			if(ins!=null){
				return ins;
			}
		}

		InputStream ins = PropertiesKit.class.getResourceAsStream(resource);
		return ins;

	}


}
