package org.beetl.sql.clickhouse;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.ClickHouseStyle;
import org.beetl.sql.core.nosql.SchemaLessMetaDataManager;
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
		SchemaLessMetaDataManager metaDataManager = (SchemaLessMetaDataManager)sqlManager.getMetaDataManager();
		metaDataManager.addBean(ClickHouseUser.class);
        ClickHouseUser user = sqlManager.unique(ClickHouseUser.class,"a");
//        PageRequest pageRequest = DefaultPageRequest.of(1,10);
//        sqlManager.execute(new SQLReady("select * from user "),ClickHouseUser.class,pageRequest);

    }

    public static DataSource clickhouseDatasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:clickhouse://clickhouse.svc.wenjingtech.com:18123/clinical");
        ds.setDriverClassName("ru.yandex.clickhouse.ClickHouseDriver");
        ds.setUsername("alice");
        ds.setPassword("R1K9u7im");
//        ds.set
        // ds.setAutoCommit(false);
        return ds;
    }
}
