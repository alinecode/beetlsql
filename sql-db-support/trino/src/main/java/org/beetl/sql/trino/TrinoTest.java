package org.beetl.sql.trino;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.nosql.SchemaLessMetaDataManager;
import org.beetl.sql.core.nosql.TrinoStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.math.BigDecimal;

/**
 * 参考readme
 */
public class TrinoTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new DefaultNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new TrinoStyle());
        SQLManager sqlManager = builder.build();
		TrinoOrderMapper mapper = sqlManager.getMapper(TrinoOrderMapper.class);
		//方便虚拟一个id查询
		((SchemaLessMetaDataManager)sqlManager.getMetaDataManager()).addBean(TrinoOrder.class);

//		System.out.println(sqlManager.getMetaDataManager().allTable());
//		System.out.println(sqlManager.allCount(TrinoOrder.class));
//		Long id = 4500001L;
//		TrinoOrder order = sqlManager.unique(TrinoOrder.class,id);
//		System.out.println(order);
//
//		order.setTotalPrice(new BigDecimal(23));
//		sqlManager.updateById(order);

		PageRequest pageRequest = DefaultPageRequest.of(1,5);
		PageResult<TrinoOrder> pageResult = mapper.select(pageRequest);
		System.out.println(pageResult);

		LambdaQuery<TrinoOrder> query = sqlManager.lambdaQuery(TrinoOrder.class);
		PageResult queryPageResult = query.andEq(TrinoOrder::getCustKey,31540L).page(1,10);
		System.out.println(queryPageResult);



    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:trino://127.0.0.1:8080/tpch/sf1");
        ds.setUsername("foo");
        return ds;
    }
}
