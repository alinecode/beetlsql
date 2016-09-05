package org.beetl.sql.ext.gen;

import java.io.InputStream;
import java.io.InputStreamReader;

public class GenConfig {
	//基类，默认就是Object
	public String baseClass;
	//格式控制，4个隔空
	public int spaceCount = 4;
	// double 类型采用BigDecimal
	public boolean preferBigDecimal = true ;
	//采用java.util.Date
	public boolean preferDate = true ;
	//生成mapper代码
	public String mapperPkg = null ;
	// 也生成内置的insert和update相关代码
	public boolean genAutoSql=false;
	
	/**
	 * 模板
	 */
	public static  String template =  null;
	public static String mapperTemplate=null;
	static {
		initTemplate("/org/beetl/sql/ext/gen/pojo.btl");
		initMapper("/org/beetl/sql/ext/gen/mapper.btl");
	}
	
	
	
	//对于数字，优先使用封装类型
//	private boolean preferPrimitive = false ;
	
	private boolean display = false ;
	
	public String space = "    ";
	
	public  GenConfig setBaseClass(String baseClass){
		this.baseClass = baseClass;
		return this ;
	}
	public GenConfig setSpace(int count){
		this.spaceCount = count ;
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<count;i++){
			sb.append(" ");
		}
		space = sb.toString();
		return this;
	}
	
	public GenConfig preferBigDecimal(boolean prefer){
		this.preferBigDecimal = prefer;
		return this;
	}
	
	public GenConfig preferPrimitive(boolean primitive){
		this.preferBigDecimal = primitive;
		return this;
	}
	public String getBaseClass() {
		return baseClass;
	}
	public int getSpaceCount() {
		return spaceCount;
	}
	public boolean isPreferBigDecimal() {
		return preferBigDecimal;
	}

	
	public boolean isPreferDate() {
		return preferDate;
	}
	public void setPreferDate(boolean preferDate) {
		this.preferDate = preferDate;
	}
	public void setPreferBigDecimal(boolean preferBigDecimal) {
		this.preferBigDecimal = preferBigDecimal;
	}
	public String getSpace(){
		return space;
	}
	public boolean isDisplay() {
		return display;
	}
	public GenConfig setDisplay(boolean display) {
		this.display = display;
		return this;
	}
	
	/** 使用模板文件的classpath来初始化模板
	 * @param classPath
	 */
	public static void initTemplate(String classPath){
		template = getTemplate(classPath);
	}
	/**
	 * mapper 代码生成
	 * @since 2.6.1
	 * @param classPath
	 */
	public static void initMapper(String classPath){
		mapperTemplate = getTemplate(classPath);
	}
	
	private static String getTemplate(String classPath){
		try{
			//系统提供一个pojo模板
			InputStream ins = GenConfig.class.getResourceAsStream(classPath);
			InputStreamReader reader = new InputStreamReader(ins,"utf-8");
			//todo, 根据长度来，不过现在模板不可能超过8k
			char[] buffer = new char[1024*8];
			int len = reader.read(buffer);
			return new String(buffer,0,len);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	
	/**
	 * 传入pojo模板
	 * @param temp
	 */
	public static void initStringTemplate(String temp){
		template = temp;
	}
	
	/**
	 * 传入mapper代码
	 * @param temp
	 */
	public static void initStringMapperTemplate(String temp){
		mapperTemplate = temp;
	}
	
	
	
	
	
}
