package org.beetl.sql.ext.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.beetl.core.Function;
import org.beetl.core.TagFactory;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.springframework.core.io.Resource;

/**
 * 这个类已过时，将采用SqlManagerFactoryBean构造SqlManager
 * @see SqlManagerFactoryBean
 */
public class SpringBeetlSql {
	ConnectionSource cs;
	DBStyle dbStyle;
	SQLLoader sqlLoader;
	NameConversion nc;
	Interceptor[] interceptors;
	SQLManager sqlManager ;
	
	private Map<String, Function> functions = Collections.emptyMap();
	
	private Map<String, TagFactory> tagFactorys = Collections.emptyMap();

	/**
	 * 配置文件地址
	 */
	protected Resource configFileResource = null;
	
	protected String defaultSchema = null;
	
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
			nc = new DefaultNameConversion();
		}
		
		if(interceptors==null){
			interceptors = new Interceptor[0];
		}
		
		Properties properties = new Properties();
		if(this.configFileResource!=null){
			
			if (configFileResource != null)
			{
				InputStream in = null;
				try
				{
					// 如果指定了配置文件，先加载配置文件

					in = configFileResource.getInputStream();
					properties.load(in);
				}
				catch (IOException ex)
				{
					throw new RuntimeException(ex);
				}
				finally
				{
					if (in != null)
					{
						try {
							in.close();
						} catch (IOException e) {
							
						}
						in = null;
					}
				}
			}
		}
		sqlManager = new SQLManager(dbStyle,sqlLoader,cs,nc,interceptors,this.defaultSchema,properties);
		
		
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
	
	
	public SQLManager getSQLManager(){
		return this.sqlManager;
	}
	
	/** 错误的拼写，请使用getSQLManager，囧
	 * @return
	 */
	@Deprecated
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

	public Resource getConfigFileResource() {
		return configFileResource;
	}

	public void setConfigFileResource(Resource configFileResource) {
		this.configFileResource = configFileResource;
	}

	public String getDefaultSchema() {
		return defaultSchema;
	}

	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}
	
	

}
	