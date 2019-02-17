package org.beetl.sql.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.handler.JsonHandler;
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

        SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
        DebugInterceptor debug = new DebugInterceptor();

        Interceptor[] inters = new Interceptor[] { debug };
        final SQLManager sql = new SQLManager(style, loader, cs, new UnderlinedNameConversion(), inters);
        //预先注册一个，否则没有办法使用@Jackson注解
        sql.getBeetl().getGroupTemplate().registerFunction("jackson", JsonHandler.json);
        // sql.genPojoCodeToConsole("user", "com.test");
//        sql.addVirtualTable("user_1","user");
        UserDao dao = sql.getMapper(UserDao.class);
//        User user = new User();
//        user.setName("abcd");
//        Role role = new Role();
//        role.setId(1);
//        role.setName("whatever");
//        user.setRole(role);
//        dao.insert(user,true);

        User user = dao.unique(22);
        System.out.println(user.getRole().getName());
//        User user = dao.unique(8);
//        user.setName("99999");
//        dao.updateById(user);
        System.out.println(user.getId());
//        List<User> users = dao.all();
//        System.out.println(users.get(0).getDepartment().getName());

//        List<User> list =  sql.all(User.class);
//        System.out.println(list.get(0).getName());


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
