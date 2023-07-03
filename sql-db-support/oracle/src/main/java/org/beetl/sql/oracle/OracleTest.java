package org.beetl.sql.oracle;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.db.Oracle12Style;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.oracle.Employee;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;
import java.util.Set;

/**
 * https://hub.docker.com/r/oracleinanutshell/oracle-xe-11g,使用oralce默认的示例库 HR
 * @author lijiazhi
 */
public class OracleTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new Oracle12Style());
        SQLManager sqlManager = builder.build();


		{
			String sql = "SELECT e.* FROM scott.EMP e  ";
			PageRequest pageRequest = DefaultPageRequest.of(1,5);
			PageResult<Employee> list = sqlManager.execute(new SQLReady(sql),Employee.class,pageRequest);
		}

		{
			String sql = "SELECT #{page()} FROM scott.EMP e  ";
			PageRequest pageRequest = DefaultPageRequest.of(1,10);
			PageResult<Employee> list = sqlManager.executePageQuery(sql,Employee.class,null,pageRequest);
		}



    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe");
        ds.setUsername("system");
        ds.setPassword("oracle");
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        return ds;
    }
}
