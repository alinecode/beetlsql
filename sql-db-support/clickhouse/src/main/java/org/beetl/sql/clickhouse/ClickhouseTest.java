package org.beetl.sql.clickhouse;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.ClickHouseStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;


import javax.sql.DataSource;
import java.util.UUID;

public class ClickhouseTest {

    public static void main(String[] args){
        DataSource dataSource = clickhouseDatasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new ClickHouseStyle());
        SQLManager sqlManager = builder.build();

        ClickHouseUser user = new ClickHouseUser();
        String uuid = UUID.randomUUID().toString();
        user.setId(uuid);
        user.setCreateTs(System.currentTimeMillis());
        user.setName("testName");
        user.setDay(new java.sql.Date(System.currentTimeMillis()));
        sqlManager.insert(user);
        user = sqlManager.unique(ClickHouseUser.class,uuid);

        PageRequest pageRequest = DefaultPageRequest.of(1,10);
        sqlManager.execute(new SQLReady("select * from user "),ClickHouseUser.class,pageRequest);

    }

    public static DataSource clickhouseDatasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:clickhouse://192.168.20.127:8123/test");
        ds.setDriverClassName("ru.yandex.clickhouse.ClickHouseDriver");
        // ds.setAutoCommit(false);
        return ds;
    }
}
