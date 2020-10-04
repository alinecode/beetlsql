package org.beetl.sql.taos;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.NoSchemaMetaDataManager;
import org.beetl.sql.core.nosql.TaosStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

public class TaosTest {
    public static void main(String[] args){
        String str = System.getProperty("java.library.path");
        System.out.println(str);
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new TaosStyle());
        SQLManager sqlManager = builder.build();
        NoSchemaMetaDataManager metaDataManager = (NoSchemaMetaDataManager)sqlManager.getMetaDataManager();
        metaDataManager.addBean(Data.class);

        List<Data> datas = sqlManager.all(Data.class);
        Data data = new Data();
        data.setA(1);
        data.setTs(new Timestamp(System.currentTimeMillis()));
        sqlManager.insert(data);

        PageRequest page = DefaultPageRequest.of(1,3);
       PageResult<Data> result =  sqlManager.execute(new SQLReady("select * from t "),Data.class,page);
       System.out.println(result);



    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:TAOS://127.0.0.1:6030/db");
        ds.setUsername("root");
        ds.setPassword("taosdata");
        ds.setDriverClassName("com.taosdata.jdbc.TSDBDriver");
        return ds;
    }
}
