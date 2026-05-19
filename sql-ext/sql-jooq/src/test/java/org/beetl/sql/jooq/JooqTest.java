package org.beetl.sql.jooq;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.ext.DBInitHelper;
import org.beetl.sql.ext.DebugInterceptor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import java.util.List;

import static org.beetl.sql.jooq.JooqCodeGenHelper.defaultTargetPackage;
import static org.beetl.sql.jooq.gen.tables.OrderLogQuery.ORDER_LOG;

public class JooqTest {
	SQLManager sqlManager = null;
	@Before
	public void init() throws Exception {
		sqlManager = getSQLManager();
		DBInitHelper.executeSqlScript(sqlManager, "db/schema.sql");

		JooqCodeGenHelper codeGen = new JooqCodeGenHelper("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE", "sa", ""
			, "org.h2.Driver", defaultTargetPackage, "./src/test/java");

		codeGen.genCode(null, "PUBLIC", false);
	}

	@Test
	public void testJooq() throws Exception {

		{
			DSLContext create = DSL.using(dataSource.getConnection(), SQLDialect.H2);
			String sql = create.select().from(ORDER_LOG).getSQL();
			System.out.println(sql);
			Result<Record> result = create.select().from(ORDER_LOG).fetch();
			for (org.jooq.Record r : result) {
				Integer id = r.getValue(ORDER_LOG.ORDER_ID);
				Integer version = r.getValue(ORDER_LOG.VERSION);
				ReLog log = r.into(ReLog.class);
				System.out.println("ID: " + id + " version: " + version);
				System.out.println("ID: " + log.getOrderId() + " version: " + log.getVersion());

			}
		}
	}

	@Test
	public void testJooqHelper() throws Exception {
		JooqHelper jooqHelper = new JooqHelper(sqlManager);
		{
			ReLog reLog= jooqHelper.queryOne(ReLog.class,
				create -> create.select().from(ORDER_LOG).where(ORDER_LOG.VERSION.eq(100))
			);
			Assert.assertEquals(100,reLog.getVersion().intValue());
		}
		{
			List<ReLog> lists = jooqHelper.query(ReLog.class,
				create -> create.select().from(ORDER_LOG).where(ORDER_LOG.VERSION.eq(100))
			);
			Assert.assertEquals(1,lists.size());
		}
	}

	@Table(name="order_log")
	public static class ReLog {
		@AssignID
		Integer orderId;
		Integer version;

		public Integer getOrderId() {
			return orderId;
		}

		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}

		public Integer getVersion() {
			return version;
		}

		public void setVersion(Integer version) {
			this.version = version;
		}

		@Override
		public String toString() {
			return "ReLog{" +
				"orderId=" + orderId +
				", version=" + version +
				'}';
		}
	}

	static DataSource dataSource = datasource();

	private static DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDriverClassName("org.h2.Driver");
		ds.setSchema(null);
		return ds;
	}

	private static SQLManager getSQLManager() {

		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style() {
			public int getMaxBatchCount() {
				return 10;
			}
		});
		builder.setProduct(false);
		SQLManager sqlManager = builder.build();
		return sqlManager;
	}
}
