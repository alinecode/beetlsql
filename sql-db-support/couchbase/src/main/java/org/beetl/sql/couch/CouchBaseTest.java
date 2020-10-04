package org.beetl.sql.couch;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.*;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

/**
 * https://docs.couchbase.com/server/6.5/install/getting-started-docker.html
 * @author xiandafu
 */
public class CouchBaseTest {
    public static void main(String[] args) throws Exception{
        DataSource dataSource = dataSource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);


        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new CouchBaseStyle());
        SQLManager sqlManager = builder.build();

        TableDesc desc =  sqlManager.getMetaDataManager().getTable("beer-sample");
        String id = "21st_amendment_brewery_cafe";
        BeerSample data = sqlManager.unique(BeerSample.class,id);
        System.out.println(data);


        PageRequest pageRequest = DefaultPageRequest.of(1,10);
        PageResult result = sqlManager.execute(new SQLReady("select * from `beer-sample` "),BeerSample.class,pageRequest);
        System.out.println(result);

    }

    public static DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:cdata:couchbase:User='root';Password='12345678';Server='http://127.0.0.1'");
        ds.setDriverClassName("cdata.jdbc.couchbase.CouchbaseDriver");
        return ds;
    }
}
