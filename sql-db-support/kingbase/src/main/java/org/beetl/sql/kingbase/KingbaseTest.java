package org.beetl.sql.kingbase;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.KingbaseStyle;
import org.beetl.sql.core.nosql.ClickHouseStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

public class KingbaseTest {
    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new KingbaseStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());

//        UserInfo userInfo = new UserInfo();
//        userInfo.setId(2);
//        userInfo.setName("ok");
//        userInfo.setCreateDate(new Timestamp(System.currentTimeMillis()));

//        List<UserInfo> list =sqlManager.all(UserInfo.class);
//
//        System.out.println(list.size());

        PageRequest pageRequest = DefaultPageRequest.of(1,10);
        sqlManager.execute(new SQLReady("select * from user_info "),UserInfo.class,pageRequest);





    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:kingbase8://192.168.20.154:54321/TEST");
        ds.setDriverClassName("com.kingbase8.Driver");
        ds.setUsername("SYSTEM");
        ds.setPassword("12345678");
        // ds.setAutoCommit(false);
        return ds;
    }
}
