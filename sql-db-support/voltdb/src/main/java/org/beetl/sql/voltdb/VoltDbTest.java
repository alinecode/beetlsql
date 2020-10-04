package org.beetl.sql.voltdb;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 没成功，https://docs.voltdb.com/UsingVoltDB/ProgLangjdbc.php
 */
@Deprecated
public class VoltDbTest {
    public static void main(String[] args) throws SQLException {
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MySqlStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());




    }




    public static DataSource datasource() {

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:voltdb://127.0.0.1:32822");
        ds.setDriverClassName("org.voltdb.jdbc.Driver");
        return ds;
    }
}
