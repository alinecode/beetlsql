package org.beetl.sql.gauss;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.db.OpenGaussStyle;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * @see "https://zhuanlan.zhihu.com/p/159041469"
 * @see "https://www.modb.pro/db/31169"
 */
public class GaussTest {
    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new OpenGaussStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());

        List<HuaweiDb> list = sqlManager.all(HuaweiDb.class);
        System.out.println(list.size());

        HuaweiDb db = new HuaweiDb();
        db.setName("abc");
        db.setCreateTime(new Date());
        sqlManager.insert(db);

    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:8888/enmotech");
        ds.setUsername("enmotech");
        ds.setPassword("Enm0t3ch");
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
