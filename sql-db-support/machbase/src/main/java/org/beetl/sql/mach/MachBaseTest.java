package org.beetl.sql.mach;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.HiveStyle;
import org.beetl.sql.core.nosql.MachbaseStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

public class MachBaseTest {
    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MachbaseStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());

        List<Tag> tags =  sqlManager.all(Tag.class);

//        Tag tag = new Tag();
//        tag.setName("device");
//        tag.setTime(new Timestamp(System.currentTimeMillis()));
//        tag.setValue(1.2);
//        sqlManager.insert(tag);

//        Tag tag = sqlManager.unique(Tag.class,"device");
//        System.out.println(tag);


        PageRequest page = DefaultPageRequest.of(1,5);
        PageResult<Tag> pageResult = sqlManager.execute(
                new SQLReady("select * from tag  "),Tag.class,page);
        System.out.println(pageResult);




    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:machbase://127.0.0.1:5656/mhdb");
        ds.setDriverClassName("com.machbase.jdbc.driver");
        ds.setUsername("sys");
        ds.setPassword("manager");
        return ds;
    }
}
