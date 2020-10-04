import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.OpenGaussStyle;
import org.beetl.sql.core.db.PolarDBStyle;
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
public class PolarDBTest {
    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new PolarDBStyle());
        SQLManager sqlManager = builder.build();
        System.out.println(sqlManager.getMetaDataManager().allTable());

        List<AliyUser> list = sqlManager.all(AliyUser.class);
        System.out.println(list.size());

        AliyUser db = new AliyUser();
        db.setId(2);
        db.setName("abc");
        db.setCreateDate(new Date());
        sqlManager.insert(db);


		PageRequest pageRequest = DefaultPageRequest.of(1,10);
		sqlManager.execute(new SQLReady("select * from user "),AliyUser.class,pageRequest);
		System.out.print(pageRequest);

	}

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://192.168.20.147:5444/benchmarksql");
        ds.setUsername("benchmarksql");
        ds.setPassword("benchmark");
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
