package org.beetl.sql.drill;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.DrillStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DrillTest {
    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new DrillStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());
        String sql =" SELECT employee_id, full_name, position_id, " +
                "position_title FROM cp.`employee.json`  where full_name=#{name} ";

        Map map= new HashMap();
        map.put("name","abc");
        sqlManager.execute(sql,Map.class,map);

        PageRequest page = DefaultPageRequest.of(1,5);
//        PageResult result = sqlManager.execute(new SQLReady(sql,"abc"),Map.class,page);
//        System.out.println(result);

    }

    public static DataSource datasource() {

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:drill:drillbit=127.0.0.1");
//        ds.setUsername("root");
//        ds.setPassword("taosdata");
        ds.setDriverClassName("org.apache.drill.jdbc.Driver");
        return ds;
    }
}
