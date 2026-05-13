package org.beetl.sql.iotdb;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.ClickHouseStyle;
import org.beetl.sql.core.nosql.IotDbStyle;
import org.beetl.sql.core.nosql.NoSchemaMetaDataManager;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * <pre>
 *     CREATE TIMESERIES root.test2.wf01.wt01.status WITH DATATYPE=BOOLEAN, ENCODING=PLAIN
 *     CREATE TIMESERIES root.test2.wf01.wt01.json WITH DATATYPE=TEXT, ENCODING=PLAIN
 *
 *
 *
 *     INSERT INTO root.test2.wf01.wt01(timestamp,status,json) values(200,false,"20.71")
 *     INSERT INTO root.test2.wf01.wt01(timestamp,status,json) values(300,true,"121.09")
 * </pre>
 *
 * @see "https://iotdb.apache.org/zh/UserGuide/latest/SQL-Manual/QuickStart-Only-Sql_apache.html"
 */
public class IotDbTest {
	public static void main(String[] args){
		DataSource dataSource = datasource();
		ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
		SQLManagerBuilder builder = new SQLManagerBuilder(source);
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new DebugInterceptor()});
		builder.setDbStyle(new IotDbStyle());
		SQLManager sqlManager = builder.build();
		System.out.println(sqlManager.getMetaDataManager().allTable());
		NoSchemaMetaDataManager noSchemaMetaDataManager = (NoSchemaMetaDataManager)sqlManager.getMetaDataManager();
		noSchemaMetaDataManager.addBean(IotData.class);
		List<IotData> all = sqlManager.all(IotData.class);
		System.out.println(all);
//
//
		IotData data = new IotData();
		data.setTimestamp(new Timestamp(112));
		data.setStatus(true);
		data.setJson("abcddf");
		sqlManager.insert(data);

//
//
//
		PageRequest pageRequest = DefaultPageRequest.of(1,10);
		sqlManager.execute(new SQLReady("select status from root.test2.wf01.wt01 "),IotData.class,pageRequest);

		String template = "select ${page()} from root.test2.wf01.wt01";
		PageResult pageResult = sqlManager.executePageQuery(template,IotData.class,new HashMap<>(),pageRequest);
		System.out.println(pageResult);


	}

	public static DataSource datasource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:iotdb://127.0.0.1:6667/");
		ds.setDriverClassName("org.apache.iotdb.jdbc.IoTDBDriver");
		ds.setUsername("root");
		ds.setPassword("root");
		return ds;
	}
}
