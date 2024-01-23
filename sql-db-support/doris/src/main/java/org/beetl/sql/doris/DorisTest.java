package org.beetl.sql.doris;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.DorisStyle;
import org.beetl.sql.core.nosql.SchemaLessMetaDataManager;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;

public class DorisTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new DorisStyle());
        SQLManager sqlManager = builder.build();
		((SchemaLessMetaDataManager)sqlManager.getMetaDataManager()).addBean(DorisUser.class);
		DorisMapper dorisMapper = sqlManager.getMapper(DorisMapper.class);
//		sqlManager.all(DorisUser.class);
		sqlManager.unique(DorisUser.class,1);

//		DorisUser user = new DorisUser();
//		user.setUserId(2);
//		user.setAge(1);
//		user.setDate(new Date(System.currentTimeMillis()));
//		user.setCity("beijing");
//		user.setCost(12323L);
//		user.setLastVisitDate(new Timestamp(System.currentTimeMillis()));
//		user.setMaxDwellTime(13434);
//		user.setMinDwellTime(14);
//
//
//
//		sqlManager.insert(user);
//
////		user.setAge(24);
////		sqlManager.updateById(user);
//

//		dorisMapper.deleteById(2);



//		DorisUser use2 = new DorisUser();
//		use2.setUserId(3);
//		use2.setAge(1);
//		use2.setDate(new Date(System.currentTimeMillis()));
//		use2.setCity("beijing");
//		use2.setCost(12323L);
//		use2.setLastVisitDate(new Timestamp(System.currentTimeMillis()));
//		use2.setMaxDwellTime(13434);
//		use2.setMinDwellTime(14);
//
//		DorisUser use3 = new DorisUser();
//		use3.setUserId(4);
//		use3.setAge(1);
//		use3.setDate(new Date(System.currentTimeMillis()));
//		use3.setCity("beijing");
//		use3.setCost(12323L);
//		use3.setLastVisitDate(new Timestamp(System.currentTimeMillis()));
//		use3.setMaxDwellTime(13434);
//		use3.setMinDwellTime(14);
//		sqlManager.insertBatch(DorisUser.class, Arrays.asList(use2,use3));

//		List<DorisUser> list = sqlManager.execute(new SQLReady("select * from example_tbl_agg1 order by user_id limit 1,2"),DorisUser.class);

//		PageResult<DorisUser>  pageResult =  dorisMapper.select(DefaultPageRequest.of(1,5));


    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:9030/test");
        ds.setUsername("root");
        ds.setPassword("");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }
}
