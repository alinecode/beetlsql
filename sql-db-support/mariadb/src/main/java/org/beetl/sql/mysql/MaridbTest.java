package org.beetl.sql.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

/**
 * 测试 存储过程支持
 * <pre>
 *
 *     docker pull mariadb:5.5.68
 *     docker run --name mariadb -e MYSQL_ROOT_PASSWORD=mypass -p 13306:3306 -d mariadb:10.4
 *
 *
 * </pre>
 *
 * 存储过程
 * <pre>
 *     CREATE PROCEDURE test.logcount(eid int,out total int(4))
 * 		BEGIN
 * 			set total=1;
 * 		END
 * </pre>
 */
public class MaridbTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MySqlStyle());
        SQLManager sqlManager = builder.build();
		MyTestMapper mapper = sqlManager.getMapper(MyTestMapper.class);
		OutHolder outHolder = new OutHolder();
		mapper.logcount(1,outHolder);
		System.out.println(outHolder.getId());


    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mariadb://127.0.0.1:13306/test");
        ds.setUsername("root");
        ds.setPassword("mypass");
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        return ds;
    }
}
