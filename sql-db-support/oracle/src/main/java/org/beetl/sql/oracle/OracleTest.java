package org.beetl.sql.oracle;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
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
        builder.setDbStyle(new OracleStyle());
        SQLManager sqlManager = builder.build();
		Set<String> tables = sqlManager.getMetaDataManager().allTable();


//		List<Employee> list = sqlManager.all(Employee.class);
//		System.out.println(list);

//		Employee employee = sqlManager.unique(Employee.class,100);
//		System.out.println(employee.getHireDate());
		long time = System.currentTimeMillis();
		Date start = new Date(time-100);
		Date end = new Date(time+100);
		long count = sqlManager.lambdaQuery(Employee.class).andBetween(Employee::getHireDate,start,end).count();


    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:oracle:thin:@127.0.0.1:49161:xe");
        ds.setUsername("system");
        ds.setPassword("oracle");
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        return ds;
    }
}
