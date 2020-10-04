package org.beetl.sql.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

public class MysqlTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MySqlStyle());
        SQLManager sqlManager = builder.build();

//        MysqlUser user = new MysqlUser();
//
//        user.setCreateTs(System.currentTimeMillis());
//        user.setName("testName");
//        user.setDay(new java.sql.Date(System.currentTimeMillis()));
//        sqlManager.insert(user);
//        user = sqlManager.unique(MysqlUser.class,user.getId());
//
//        PageRequest pageRequest = DefaultPageRequest.of(1,10);
//        sqlManager.execute(new SQLReady("select * from user "),MysqlUser.class,pageRequest);

         MyUserMapper userMapper = sqlManager.getMapper(MyUserMapper.class);
        userMapper.select();

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:13306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8");
        ds.setUsername("root");
        ds.setPassword("12345678");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }
}
