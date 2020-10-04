package org.beetl.sql.hbase;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.HBaseStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.util.List;

/**
 * 参考 {@see "https://github.com/smizy/docker-apache-phoenix"}
 * 需要修改docckder-compose.yml,导出所有端口到宿主机，并修改宿主机hosts，添加
 * 127.0.0.1	regionserver-1.vnet
 * 127.0.0.1	hmaster-1.vnet
 * 否则jdbc无法链接
 *
 */
public class HBaseTest {
    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new HBaseStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());


        List<WebStat> webStatList = sqlManager.all(WebStat.class);

        List<Stock> list = sqlManager.all(Stock.class);
        Stock stock = list.get(0);

        sqlManager.execute(new SQLReady("select * from STOCK_SYMBOL where symbol='CRM' "),Stock.class);

        sqlManager.executeUpdate(new SQLReady("UPSERT INTO STOCK_SYMBOL " +
                "(SYMBOL, COMPANY) VALUES (?,?)","CRM","GOOGLE"));
        sqlManager.updateById(stock);

        Stock stock1 = new Stock();
        stock1.setSymbol("abcd");
        stock1.setCompany("apache");
        sqlManager.insert(stock1);

        PageRequest page = DefaultPageRequest.of(1,5);
        PageResult pageResult = sqlManager.execute(new SQLReady("select * from STOCK_SYMBOL ")
                ,Stock.class,page);
         pageResult = sqlManager.executePageQuery("select #{page()} from STOCK_SYMBOL"
                ,Stock.class,null,page);

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:phoenix:127.0.0.1:2181");
//        ds.setUsername("root");
//        ds.setPassword("taosdata");
        ds.setDriverClassName("org.apache.phoenix.jdbc.PhoenixDriver");
        return ds;
    }
}