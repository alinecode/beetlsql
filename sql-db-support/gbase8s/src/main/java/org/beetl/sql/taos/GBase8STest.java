package org.beetl.sql.taos;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.GreatSqlStyle;
import org.beetl.sql.core.nosql.NoSchemaMetaDataManager;
import org.beetl.sql.core.nosql.TaosStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @see "https://hub.docker.com/r/liaosnet/gbase8s"
 */
public class GBase8STest {
    public static void main(String[] args){
        ConnectionSource source = getCs();

        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new GreatSqlStyle());
        SQLManager sqlManager = builder.build();

		System.out.println(sqlManager.getMetaDataManager().allTable());
		Company company = new Company();
		company.setName("hi");
		company.setAge(12);
		company.setJoinDate(new Date());
		sqlManager.insert(company);

		String sql = "select * from company";
		PageRequest pageRequest = DefaultPageRequest.of(1,4);
		PageResult<Company> pageResult = sqlManager.execute(new SQLReady(sql),Company.class,pageRequest);

		System.out.println(pageResult);





    }

    public static ConnectionSource getCs(){
    	String driver = "com.gbasedbt.jdbc.Driver";
    	String url = "jdbc:gbasedbt-sqli://127.0.0.1:19088/testdb:GBASEDBTSERVER=gbase01;DB_LOCALE=zh_CN.utf8;CLIENT_LOCALE=zh_CN.utf8;IFX_LOCK_MODE_WAIT=30;";
    	String user = "gbasedbt";
    	String password ="GBase123";
		return ConnectionSourceHelper.getSimple(driver,url,user,password);

	}

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:gbasedbt-sqli://127.0.0.1:19088/testdb:GBASEDBTSERVER=gbase01;DB_LOCALE=zh_CN.utf8;CLIENT_LOCALE=zh_CN.utf8;IFX_LOCK_MODE_WAIT=30;");
        ds.setUsername("gbasedbt");
        ds.setPassword("GBase123");
        ds.setDriverClassName("com.gbasedbt.jdbc.Driver");
        return ds;
    }
}
