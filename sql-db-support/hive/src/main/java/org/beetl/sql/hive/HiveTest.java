package org.beetl.sql.hive;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.HBaseStyle;
import org.beetl.sql.core.nosql.HiveStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 参考 {@see "https://github.com/big-data-europe/docker-hive"}
 * 遗留问题，hive的子查询结果，比如 select * from (select id from c) t,jdbc返回结果应该是id，但他返回了t.id
 * 因此HiveStyle需要重新实现BeanProcessor#getColName方法，返回正确的列名.
 * @author xiandafu
 */
public class HiveTest {
    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new HiveStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());

//        Dept dept = new Dept();
//        dept.setDeptNo(1);
//        dept.setAddr("地址");
//        dept.setTel("1800");
//        dept.setCreateTs(new Date(System.currentTimeMillis()));
//
//        sqlManager.insert(dept);
//
//        List<Dept> all= sqlManager.all(Dept.class);
//        System.out.println(all.size());
//
//
//        List<Dept> list =sqlManager.execute(new SQLReady("select * from dept where dept_no=1"),Dept.class);

        PageRequest page = DefaultPageRequest.of(1,5);
        // HiveStyle翻页，需要一个id来排序翻页，参考HiveStyle
//        PageResult pageResult = sqlManager.execute(
//                new SQLReady("select dept_no id,d.* from dept d "),Dept.class,page);
//        System.out.println(pageResult);


        String template = "select #{page('dept_no id,d.*')} from dept d";
        PageResult  pageResult = sqlManager.executePageQuery(template,Dept.class,null,page);
        System.out.println(pageResult);

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:hive2://127.0.0.1:10000/default");
        ds.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        return ds;
    }
}