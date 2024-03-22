package org.beetl.sql.oceanbase;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

/**
 *  oceanbase 启动
 * https://cloud.tencent.com/developer/article/2192061
 */
public class OceanBaseTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MySqlStyle());
        SQLManager sqlManager = builder.build();
		System.out.println(sqlManager.getMetaDataManager().allTable());
        OceanBaseUser user = new OceanBaseUser();

        user.setName("testName");
        sqlManager.insert(user);
        user = sqlManager.unique(OceanBaseUser.class,user.getId());

        PageRequest pageRequest = DefaultPageRequest.of(1,10);
        sqlManager.execute(new SQLReady("select * from my_user "),OceanBaseUser.class,pageRequest);

         MyUserMapper userMapper = sqlManager.getMapper(MyUserMapper.class);
         userMapper.select();

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:2881/obs?&useSSL=false");
        ds.setUsername("root@xybdiysql");
//        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }
}
