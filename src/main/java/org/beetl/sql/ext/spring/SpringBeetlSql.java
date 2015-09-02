package org.beetl.sql.ext.spring;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.beetl.core.Function;
import org.beetl.core.TagFactory;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;

public class SpringBeetlSql {
	ConnectionSource cs;
	DBStyle dbStyle;
	SQLLoader sqlLoader;
	NameConversion nc;
	Interceptor[] interceptors;
	SQLManager sqlManager ;
	
	private Map<String, Function> functions = Collections.emptyMap();
	
	private Map<String, TagFactory> tagFactorys = Collections.emptyMap();

	
	//  beetl 相关 方法
	
	public SpringBeetlSql(){}
	
	@PostConstruct
	public void init(){
		if(dbStyle==null){
			dbStyle = new MySqlStyle();
		}
		
		if(sqlLoader==null){
			sqlLoader = new ClasspathLoader("/sql");
		}
		
		if(nc==null){
			nc = new HumpNameConversion();
		}
		
		if(interceptors==null){
			interceptors = new Interceptor[0];
		}
		sqlManager = new SQLManager(dbStyle,sqlLoader,cs,nc,interceptors);
		
		
			for(Entry<String,Function> entry :functions.entrySet()){
				sqlManager.getBeetl().getGroupTemplate().registerFunction(entry.getKey(),entry.getValue());
			}
		for(Entry<String,TagFactory> entry:tagFactorys.entrySet()){
			sqlManager.getBeetl().getGroupTemplate().registerTagFactory(entry.getKey(), entry.getValue());
		}
		
	}

	public ConnectionSource getCs() {
		return cs;
	}

	public void setCs(ConnectionSource cs) {
		this.cs = cs;
	}

	public DBStyle getDbStyle() {
		return dbStyle;
	}

	public void setDbStyle(DBStyle dbStyle) {
		this.dbStyle = dbStyle;
	}

	public SQLLoader getSqlLoader() {
		return sqlLoader;
	}

	public void setSqlLoader(SQLLoader sqlLoader) {
		this.sqlLoader = sqlLoader;
	}

	public NameConversion getNc() {
		return nc;
	}

	public void setNc(NameConversion nc) {
		this.nc = nc;
	}

	public Interceptor[] getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(Interceptor[] interceptors) {
		this.interceptors = interceptors;
	}
	
	public SQLManager getSQLMananger(){
		return this.sqlManager;
	}

	public Map<String, Function> getFunctions() {
		return functions;
	}

	public void setFunctions(Map<String, Function> functions) {
		this.functions = functions;
	}

	public Map<String, TagFactory> getTagFactorys() {
		return tagFactorys;
	}

	public void setTagFactorys(Map<String, TagFactory> tagFactorys) {
		this.tagFactorys = tagFactorys;
	}

}
	