package org.beetl.sql.jmh.loader;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.engine.template.SQLTemplate;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.ext.DebugInterceptor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 测试sql加载速度
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class BeetlSQLLoaderTest {
	DataSource ds = null;
	SqlId sqlId = SqlId.of("fund","selectTest");

	@Benchmark
	public SQLManager initSQLManager() {
		ConnectionSource source = ConnectionSourceHelper.getSingle(ds);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		builder.setProduct(false);
		builder.setSqlLoader(new MarkdownClasspathLoader("loader"));
		SQLManager sqlManager = builder.build();
		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
		beetlTemplateEngine.getBeetl().getGroupTemplate().registerFunction("NotEmptyColl", new Function() {
			@Override
			public Object call(Object[] paras, Context ctx) {
				return true;
			}
		});
		return sqlManager;
	}

	@Benchmark
	public SQLTemplate parseSql() {
		SQLManager sqlManager = initSQLManager();
		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
		SQLTemplate sqlTemplate = beetlTemplateEngine.getSqlTemplate(sqlId);
		return sqlTemplate;
	}


	@Benchmark
	public SQLResult runSql() {
		SQLManager sqlManager = initSQLManager();
		Map map = new HashMap<>();
		map.put("orderBy","weekChange");
		map.put("desc",0);
		map.put("opId",1);
		map.put("orgId",1);
		map.put("fundTypes", Arrays.asList("1","2","3","4"));
		map.put("copyTradeStatus",Arrays.asList("1","2","3","4"));
		map.put("redemptionPayPeriodList",Arrays.asList("1","2","3","4"));
		SQLResult result = sqlManager.getSQLResult(SqlId.of("fund","selectTest"),map);
		return result;

	}

	@Setup
	public void setup(){
		this.ds = datasource();

	}

	private static DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");
		ds.setSchema(null);
		return ds;
	}

	public static void main(String[] args) throws RunnerException {

		Options opt = new OptionsBuilder()
			.include(BeetlSQLLoaderTest.class.getSimpleName()).forks(0)
			.build();
		new Runner(opt).run();
//		 test();
	}

	public static void test(){
		BeetlSQLLoaderTest test = new BeetlSQLLoaderTest();
		test.setup();
		SQLResult sqlResult = (SQLResult)test.runSql();
		System.out.println(sqlResult.jdbcSql);
		System.out.println(sqlResult.jdbcPara);
	}

}
