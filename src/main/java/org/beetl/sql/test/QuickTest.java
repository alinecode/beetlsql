package org.beetl.sql.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author xiandafu
 *
 */

public class QuickTest {

    public static void main(String[] args) throws Exception {

        String javaVersion = System.getProperty("java.version");
        System.out.println(javaVersion);

        // DB2SqlStyle style = new DB2SqlStyle();
        // SqlServerStyle style = new SqlServerStyle();
        // SqlServer2012Style style = new SqlServer2012Style();
        // OracleStyle style = new OracleStyle();
        MySqlStyle style = new MySqlStyle();
        // PostgresStyle style = new PostgresStyle();
        ConnectionSource cs = ConnectionSourceHelper.getSingle(datasource());

        SQLLoader loader = new ClasspathLoader("/sql");
        DebugInterceptor debug = new DebugInterceptor();

        Interceptor[] inters = new Interceptor[] { debug };
        final SQLManager sql = new SQLManager(style, loader, cs, new UnderlinedNameConversion(), inters);
        // sql.genPojoCodeToConsole("user", "com.test");
        sql.executeUpdate("update user set create_date=#date()# where id = 9",new HashMap());
//        List<User> users = dao.all();
//        System.out.println(users.get(0).getDepartment().getName());
        
//        List<User> list =  sql.all(User.class);
//        System.out.println(list.get(0).getName());

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    sql.refresh();
//                }
//
//            }
//        }).start();
//        while(true){
//            sql.select("user.selectAll",User.class, Params.ins().add("id",1).map());
//
//        }

    }

    public static User unique(SQLManager sql, Object key) {
        return sql.unique(User.class, key);
    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(MysqlDBConfig.url);
        ds.setUsername(MysqlDBConfig.userName);
        ds.setPassword(MysqlDBConfig.password);
        ds.setDriverClassName(MysqlDBConfig.driver);
        // ds.setAutoCommit(false);
        return ds;
    }

    public static DataSource druidSource() {
        com.alibaba.druid.pool.DruidDataSource ds = new com.alibaba.druid.pool.DruidDataSource();
        ds.setUrl(MysqlDBConfig.url);
        ds.setUsername(MysqlDBConfig.userName);
        ds.setPassword(MysqlDBConfig.password);
        ds.setDriverClassName(MysqlDBConfig.driver);
        return ds;
    }

}
