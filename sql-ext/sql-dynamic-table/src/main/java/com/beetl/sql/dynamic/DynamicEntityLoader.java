package com.beetl.sql.dynamic;

import org.beetl.core.GroupTemplate;
import org.beetl.core.misc.BeetlUtil;
import org.beetl.core.misc.ByteClassLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.gen.SourceBuilder;
import org.beetl.sql.gen.SourceConfig;
import org.beetl.sql.gen.simple.*;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据表名，是用gen模块生成entity代码并编译成java类
 * @param <T>
 */
public class DynamicEntityLoader<T> {
	protected SQLManager sqlManager;
	protected  Map<String,Class<? extends  T>> cache = new ConcurrentHashMap<>();

	private  final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");

	//存放entity源码，不需要，未来删除
	private  static  Map<String, JavaFileObject> fileObjectMap = new ConcurrentHashMap<>();

	protected String pkg;
	protected Class<T> baseClass;

	protected ByteClassLoader loader = null;


	public DynamicEntityLoader(SQLManager sqlManager){

		this(sqlManager,"com.test001",(Class<T>) BaseEntity.class);
	}


	/**
	 *
	 * @param sqlManager
	 * @param pkg  动态表生成java类的包名
	 * @param clazz 动态表生成java类的父类，如BaseObject，也可以是别的任何类
	 */
	public DynamicEntityLoader(SQLManager sqlManager,String pkg,Class<T> clazz){
		this.sqlManager = sqlManager;
		this.pkg = pkg;
		this.baseClass = clazz;
		ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader() != null
			? Thread.currentThread().getContextClassLoader()
			: GroupTemplate.class.getClassLoader();
		this.loader = new ByteClassLoader(defaultClassLoader);
	}

	public DynamicEntityLoader(SQLManager sqlManager,String pkg,Class<T> clazz,ClassLoader classLoader){
		this.sqlManager = sqlManager;
		this.pkg = pkg;
		this.baseClass = clazz;
		this.loader = new ByteClassLoader(classLoader);
	}

	public Class<? extends  T> getDynamicEntity(String table){
		return getDynamicEntity(table,baseClass);
	}

	public Class<? extends  T> getDynamicEntity(String table,Class<T> clazz){
		Class<? extends  T> c = cache.get(table);
		if(c!=null){
			return c;
		}
		c = cache.computeIfAbsent(table, s -> {
			Class<? extends  T> newCLass =  compile(s,clazz.getName());
			return newCLass;
		});
		return c;
	}

	protected Class<? extends  T> compile(String table,String baseObject) {
		try{
			String className = sqlManager.getNc().getClassName(table);
			byte[] classByte = BeanTableAsmCode.genCode(sqlManager,table,pkg,baseClass);
			Class<? extends  T> c = (Class<? extends  T> )loader.defineClass(pkg+"."+className,classByte);

			return c;
		}catch (RuntimeException runtimeException){
			throw runtimeException;
		}catch (Exception exception){
			throw new IllegalStateException("编译class "+table+" 错误",exception);
		}
	}


}
