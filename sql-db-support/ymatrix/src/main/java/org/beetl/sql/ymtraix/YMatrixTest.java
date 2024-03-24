package org.beetl.sql.ymtraix;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.core.db.YMatrixStyle;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.util.Set;

/**
 *
 * https://ymatrix.cn/doc/4.3/install/mx4_docker
 */
public class YMatrixTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new YMatrixStyle());
        SQLManager sqlManager = builder.build();
		Set<String> set = sqlManager.getMetaDataManager().allTable();
		System.out.println(set);

		MyUser myUser = new MyUser();
		myUser.setId(2);
		myUser.setName("hello");
		sqlManager.insert(myUser);


    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://127.0.0.1:5432/postgres");
        ds.setUsername("mxadmin");
        ds.setPassword("changeme");
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
