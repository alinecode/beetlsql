package org.beetl.sql.gen.simple;

import org.beetl.core.ReThrowConsoleErrorHandler;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.gen.BaseProject;
import org.beetl.sql.gen.SourceBuilder;
import org.beetl.sql.gen.SourceConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 快速生成代码到控制台的类
 */
public class SimpleCodeTool {
	BaseProject project = new StringOnlyProject();
	SQLManager sqlManager;
	String jdbcUrl;
	String jdbcDriver;
	String userName;
	String passwd;
	DBStyle dbStyle;
	NameConversion nameConversion;

	public SimpleCodeTool(String jdbcUrl,String jdbcDriver,String userName,String passwd, DBStyle dbStyle,NameConversion nameConversion){
		this.jdbcUrl = jdbcUrl;
		this.jdbcDriver = jdbcDriver;
		this.userName = userName;
		this.passwd = passwd;
		this.dbStyle = dbStyle;
		this.nameConversion = nameConversion;
		initSQLManager();
	}

	public SimpleCodeTool(String jdbcUrl,String jdbcDriver,String userName,String passwd){
		this(jdbcUrl,jdbcDriver,userName,passwd,new MySqlStyle(),new UnderlinedNameConversion());
	}

	public SimpleCodeTool(SQLManager sqlManager){
		this.sqlManager = sqlManager;
	}



	public String code(String table){

		List<SourceBuilder> sourceBuilder = new ArrayList<>();
		SourceBuilder entityBuilder = new EntitySourceBuilder();
		SourceBuilder mapperBuilder = new MapperSourceBuilder();
		SourceBuilder mdBuilder = new MDSourceBuilder();

		sourceBuilder.add(entityBuilder);
		sourceBuilder.add(mapperBuilder);
		sourceBuilder.add(mdBuilder);

		SourceConfig config = new SourceConfig(sqlManager,sourceBuilder);

		//如果有错误，抛出异常而不是继续运行1
		EntitySourceBuilder.getGroupTemplate().setErrorHandler(new ReThrowConsoleErrorHandler() );
		config.gen(table,project);
		return ((StringOnlyProject)project).getContent();
	}

	/**
	 * 子类可继承
	 * @param config
	 */
	protected  void config(SourceConfig config){
		config.setPreferDateType(SourceConfig.PreferDateType.Timestamp);
	}

	protected  SQLManager initSQLManager(){
		ConnectionSource source = ConnectionSourceHelper.getSimple(jdbcDriver,jdbcUrl,userName,passwd);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(this.dbStyle);
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}




}
