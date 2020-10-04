package org.beetl.sql.ignite;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.nosql.DrillStyle;
import org.beetl.sql.core.nosql.TaosStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 *
 * @author xiandafu
 * @see "https://apacheignite.readme.io/docs/getting-started"
 * @see "https://apacheignite-sql.readme.io/docs/jdbc-driver"
 */
public class IgniteTest {
    public static void main(String[] args) throws SQLException{
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MySqlStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());
//        init(sqlManager);
        Person person = new Person();
        person.setId(System.currentTimeMillis());
        person.setName("anc");
        person.setCountry("BJ");
        sqlManager.insert(person);

        List<Person> personList = sqlManager.all(Person.class);
        System.out.println(personList.size());

        PageRequest pageRequest = DefaultPageRequest.of(1,10);
        PageResult<Person> result = sqlManager.execute(new SQLReady("select * from Person "),Person.class,pageRequest);
        System.out.println(result);



    }

    public static void init(SQLManager sqlManager){
        sqlManager.executeOnConnection(new OnConnection(){

            @Override
            public Object call(Connection conn) throws SQLException {
                Statement st = conn.createStatement();
//                 st.executeUpdate("CREATE TABLE Person (" +
//                " id LONG PRIMARY KEY, name VARCHAR(30), country VARCHAR(30))");
//                 return null;
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Person (id, name) VALUES (?, ?)");

                    stmt.setLong(1, 1L);
                    stmt.setString(2, "John Doe");
                    stmt.executeUpdate();

                    return null;

            }
        });
    }



    public static DataSource datasource() {

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:ignite:thin://127.0.0.1/");
        ds.setReadOnly(true);
        ds.setDriverClassName("org.apache.ignite.IgniteJdbcThinDriver");
//        ds.setTransactionIsolation("TRANSACTION_NONE");
        return ds;
    }
}
