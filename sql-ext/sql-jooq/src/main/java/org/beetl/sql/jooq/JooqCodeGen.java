package org.beetl.sql.jooq;


import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.*;

import java.sql.Connection;
import java.sql.DriverManager;

public class JooqCodeGen {
	String jdbcURL;
	String userName;
	String password;
	String jdbcDriver;
	String targetPackage;
	public JooqCodeGen(String jdbcURL, String userName, String password,
					   String jdbcDriver,String targetPackage) {
		this.jdbcURL = jdbcURL;
		this.userName = userName;
		this.password = password;
		this.jdbcDriver = jdbcDriver;
		this.targetPackage = targetPackage;

	}

	public void genCode() throws Exception {
		Connection con = getConn(jdbcURL,password,userName,jdbcDriver);
		String a = con.getCatalog();
		String b = con.getSchema();
		genCode(a,b,false);
	}

	public void genCode(boolean includeRecordObject) throws Exception {
		Connection con = getConn(jdbcURL,password,userName,jdbcDriver);
		String a = con.getCatalog();
		String b = con.getSchema();
		genCode(a,b,includeRecordObject);
	}
	public void genCode(String catalog,String schema,boolean includeRecordObject) throws Exception {

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
					.withName("org.jooq.meta.h2.H2Database")
					.withInputSchema(schema)
					.withInputCatalog(catalog)
					.withIncludes(".*")

				).withStrategy(new Strategy().withName(Main.MyGeneratorStrategy.class.getName()))
				.withTarget(new Target()
					.withPackageName(targetPackage)
					.withDirectory("./target/generated-sources/jooq")
				)
				.withGenerate(new Generate().withRecords(includeRecordObject))

			);


		GenerationTool.generate(configuration);
	}


	protected Connection getConn(String jdbcURL, String password,
								  String userName, String jdbcDriver) throws Exception {

		Class.forName(jdbcDriver);
		Connection conn = DriverManager.getConnection(jdbcURL, userName, password);
		return conn;

	}


}
