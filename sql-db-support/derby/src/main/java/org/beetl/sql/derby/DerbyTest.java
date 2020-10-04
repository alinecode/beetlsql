package org.beetl.sql.derby;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DerbyStyle;
import org.beetl.sql.core.nosql.ClickHouseStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DerbyTest {
    public static void main(String[] args) {
        DataSource dataSource = clickhouseDatasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new DerbyStyle());
        SQLManager sqlManager = builder.build();
//        init(sqlManager);
        System.out.println(sqlManager.getMetaDataManager().allTable());

        DerbyUser user =  sqlManager.unique(DerbyUser.class,1);
        user.setName("lijz");
        sqlManager.updateById(user);


        PageRequest pageRequest = DefaultPageRequest.of(1,10);
        sqlManager.execute(new SQLReady("select * from t_user_1 "),DerbyUser.class,pageRequest);

        sqlManager.executePageQuery("select #{page()} from t_user_1"
                ,DerbyUser.class,null,pageRequest);







    }

    /**
     * 内存模式，初始化数据
     * @param sqlManager
     */
    public static void init(SQLManager sqlManager) {
        sqlManager.executeOnConnection(new OnConnection<Object>() {
            @Override
            public Object call(Connection connection) throws SQLException {
                Statement statement = connection.createStatement();
                statement.execute("create table t_user_1 (id int PRIMARY KEY, name varchar(20))");
                statement.execute("insert into t_user_1 values(1,'sinboy')");
                statement.execute("insert into t_user_1 values(2,'tom')");
                return null;
            }
        });
    }

    public static DataSource clickhouseDatasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:derby:derbyDB;create=false");
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");

        return ds;
    }
}
