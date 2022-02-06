package org.beetl.sql.sample;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.page.DefaultPageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * pom.xml  中过滤了跟test相关的包，sql资源文件，想要运行起来，需要暂时去掉exclude
 *
 * @author xiandafu
 */

public class SampleHelper {
	static final String dbscript = "/sample.sql";
	static SQLManager sqlManager = init();

	/**
	 * 得到一个默认配置好的sqlManager
	 * @return
	 */
	public static SQLManager getSqlManager() {
		return sqlManager;
	}

	/**
	 * 创建一个新的SQLManager
	 * @return
	 */
	public static SQLManager init() {
		DataSource dataSource = datasource();
		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new H2Style());
		SQLManager sqlManager = builder.build();
		return sqlManager;

	}


	public static DataSource datasource() {
		HikariDataSource dataSourceConfig = new HikariDataSource();
		dataSourceConfig.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		dataSourceConfig.setUsername("sa");
		dataSourceConfig.setPassword("");
		dataSourceConfig.setDriverClassName("org.h2.Driver");
		initData(dataSourceConfig, dbscript);
		return dataSourceConfig;
	}


	protected static void initTable(DataSource dataSource, String file) {
		initData(dataSource, file);
	}

	private static void initData(DataSource ds, String file) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			String[] sqls = getSqlFromFile(file);
			for (String sql : sqls) {
				runSql(conn, sql);
			}

		} catch (SQLException sqlException) {
			throw new RuntimeException(sqlException);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqlException) {
				//ignore
			}
		}
	}

	private static String[] getSqlFromFile(String file) {
		try {
			InputStream ins = SampleHelper.class.getResourceAsStream(file);
			if (ins == null) {
				throw new IllegalArgumentException("无法加载文件 " + file);
			}
			int len = ins.available();
			byte[] bs = new byte[len];
			ins.read(bs);
			String str = new String(bs, StandardCharsets.UTF_8);
			String[] sql = str.split(";");
			return sql;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	private static void runSql(Connection conn, String sql) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		ps.close();
	}

	public static void printResultSet(ResultSet rs) throws SQLException {
		/*打印列名*/
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<String> labels = new ArrayList<>(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			String columnLabel = metaData.getColumnLabel(i);
			labels.add(columnLabel);
		}
		System.out.println(String.join("  |  ", labels));
		/*打印数据*/
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		List<String> values = new ArrayList<>();
		while (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				int columnType = metaData.getColumnType(i);
				switch (columnType) {
					case Types.DATE:
						Date date = rs.getDate(i);
						if (Objects.isNull(date)) {
							values.add("null");
						} else {
							values.add(formatter.format(
									date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
						}
						break;
					case Types.TIMESTAMP:
						Timestamp timestamp = rs.getTimestamp(i);
						if (Objects.isNull(timestamp)) {
							values.add("null");
						} else {
							values.add(formatter.format(timestamp.toLocalDateTime()));
						}
						break;
					default:
						values.add(String.valueOf(rs.getObject(i)));
						break;
				}
			}
			System.out.println(String.join("  |  ", values));
			values.clear();
		}
	}

	public static void printPageResult(DefaultPageResult pageResult) {
		System.out.println(pageResult.getPage());
		System.out.println(pageResult.getPageSize());
		System.out.println(pageResult.getTotalRow());
		System.out.println(pageResult.getTotalPage());
		System.out.println(pageResult.getList());
	}

}
