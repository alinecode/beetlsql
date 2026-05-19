package org.beetl.sql.jooq;


import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.Definition;
import org.jooq.meta.jaxb.*;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.jooq.codegen.GeneratorStrategy.Mode.DEFAULT;

/**
 * 一个辅助类使用Jooq生成其需要的表、列的类和字符串常量
 * <pre>
 *JooqCodeGenHelper codeGen = new JooqCodeGenHelper("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE", "sa", ""
 * 			, "org.h2.Driver", defaultTargetPackage, "./src/main/java");
 *
 *codeGen.genCode(null, "PUBLIC", false);
 * </pre>
 * @see "org.beetl.sql.jooq.JooqTest"
 */
public class JooqCodeGenHelper {
	String jdbcURL;
	String userName;
	String password;
	String jdbcDriver;
	String targetPackage;
	String targetPath;
	public static String defaultTargetPackage = "org.beetl.sql.jooq.gen";
	public static String defaultTargetPath = "./src/main/java";
	public JooqCodeGenHelper(String jdbcURL, String userName, String password,
							 String jdbcDriver) {
		this(jdbcURL,userName,password,jdbcDriver,defaultTargetPackage,defaultTargetPath);
	}
	public JooqCodeGenHelper(String jdbcURL, String userName, String password,
							 String jdbcDriver, String targetPackage, String targetPath) {
		this.jdbcURL = jdbcURL;
		this.userName = userName;
		this.password = password;
		this.jdbcDriver = jdbcDriver;
		this.targetPackage = targetPackage;
		this.targetPath = targetPath;
	}


	public void genCode() throws Exception {
		Connection con = getConn(jdbcURL,password,userName,jdbcDriver);
		String a = con.getCatalog();
		String b = con.getSchema();
		genCode(a,b,false);
	}

	/**
	 * 使用数据库连接对应的catalog和schema 生成Jooq类
	 * @param includeRecordObject  是否包含JOOQ的Record类，默认不需要
	 * @throws Exception
	 */
	public void genCode(boolean includeRecordObject) throws Exception {
		Connection con = getConn(jdbcURL,password,userName,jdbcDriver);
		String a = con.getCatalog();
		String b = con.getSchema();
		genCode(a,b,includeRecordObject);
	}

	/**
	 *  生成代码
	 * @param catalog  选择数据库的catalog，如果没有为null
	 * @param schema   选择数据库的schema，如果没有null
	 * @param includeRecordObject 是否包含也生成Record类，默认不需要。
	 * @throws Exception
	 */
	public void genCode(String catalog,String schema,boolean includeRecordObject) throws Exception {
		String  dbType = guessDbTypeByUrl(jdbcURL);

		Configuration configuration = new org.jooq.meta.jaxb.Configuration()
			.withJdbc(new Jdbc()
				.withDriver(jdbcDriver)
				.withUrl(jdbcURL)
				.withUser(userName)
				.withPassword(password)

			)
			.withLogging(Logging.TRACE)
			.withGenerator(new Generator()
				.withDatabase(new Database()
					//org.jooq.meta.h2.H2Database
					.withName("org.jooq.meta."+dbType.toLowerCase()+"."+dbType+"Database")
					.withInputSchema(schema)
					.withInputCatalog(catalog)
					.withIncludes(".*")

				).withStrategy(new Strategy().withName(MyGeneratorStrategy.class.getName()))
				.withTarget(new Target()
					.withPackageName(targetPackage)
					.withDirectory(targetPath)
				)
				.withGenerate(new Generate().withRecords(includeRecordObject))
			);

		GenerationTool.generate(configuration);
	}
	public static class MyGeneratorStrategy  extends DefaultGeneratorStrategy {
		public String getJavaClassName(Definition definition, Mode mode){
			String name =  super.getJavaClassName(definition,mode);
			if(mode==DEFAULT){
				return name =name+"Query";
			}else{
				return name;
			}
		}
	}

	protected Connection getConn(String jdbcURL, String password,
								  String userName, String jdbcDriver) throws Exception {

		Class.forName(jdbcDriver);
		Connection conn = DriverManager.getConnection(jdbcURL, userName, password);
		return conn;

	}

	public  String guessDbTypeByUrl(String jdbcUrl) {
		if (jdbcUrl == null || jdbcUrl.isEmpty()) {
			return "UNKNOWN";
		}
		// 统一转为小写进行匹配
		String url = jdbcUrl.toLowerCase();
		if (url.contains("mysql")) {
			return "MySQL";
		} else if(url.contains("clickhouse")){
			return "ClickHouse";
		}else if(url.contains("mariadb")){
			return "MariaDB";
		}
		else if (url.contains("h2")) {
			return "H2";
		} else if (url.contains("postgresql")) {
			return "Postgres";
		}else if (url.contains("hsqldb")) {
			return "HSQLDB";
		}else if (url.contains("sqllite")) {
			return "SQLite";
		}else if (url.contains("derby")) {
			return "Derby";
		}
		//只处理jooq和beetlsql共同支持的库
		throw new IllegalArgumentException("Jooq不支持的数据库,JDBC URL "+jdbcUrl);

	}


}
