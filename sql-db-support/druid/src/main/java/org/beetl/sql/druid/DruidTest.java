package org.beetl.sql.druid;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DerbyStyle;
import org.beetl.sql.core.nosql.DruidStyle;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @see "https://druid.apache.org/docs/latest/querying/sql.html#jdbc"
 */
public class DruidTest {
    public static void main(String[] args) throws SQLException {

        String url = "jdbc:avatica:remote:url=http://localhost:8082/druid/v2/sql/avatica/";

// Set any connection context parameters you need here (see "Connection context" below).
// Or leave empty for default behavior.
        Properties connectionProperties = new Properties();

//        try (Connection connection = DriverManager.getConnection(url, connectionProperties)) {
//            try (
//                    final Statement statement = connection.createStatement();
//                    final ResultSet resultSet = statement.executeQuery("select comment from wikipedia");
//            ) {
//                while (resultSet.next()) {
//                   System.out.println(resultSet.getString(1));
//                }
//            }
//        }

        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new DruidStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());
        List<Wikipedia> list = sqlManager.all(Wikipedia.class);
        System.out.println(list.size());

    }


    public static DataSource datasource() {

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:avatica:remote:url=http://localhost:8888/druid/v2/sql/avatica/");
        ds.setDriverClassName("org.apache.calcite.avatica.remote.Driver");
//        ds.getDataSourceProperties().put("sqlQueryId","abcd");
        return ds;
    }
}