package org.beetl.sql.cassandra;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.nosql.CassandraSqlStyle;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.ext.DebugInterceptor;

import javax.sql.DataSource;
import java.util.*;

/**
 * see docker ”https://cassandra.apache.ac.cn/_/quickstart.html“
 * see jdbc driver https://github.com/ing-bank/cassandra-jdbc-wrapper
 * <pre>
 *     CREATE TABLE IF NOT EXISTS store.shopping_cart (
 *     userid int,
 *     seq int,
 *     item_count int,
 *     last_update_timestamp timestamp,
 *     -- 核心修改：在这里定义复合主键
 *     -- userid 是分区键，seq 是聚类列
 *     PRIMARY KEY (userid, seq)
 *   ) WITH CLUSTERING ORDER BY (seq ASC);
 * </pre>
 *
 *  如上为建表语句，同时考虑通过seq排序来实现上一页，下一页。
 */
public class CassandraTest {

    public static void main(String[] args){
        DataSource dataSource = datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new CassandraSqlStyle());
		SQLManager sqlManager = builder.build();
		List<ShoppingCart> list = sqlManager.all(ShoppingCart.class);
		System.out.println(list.size());

		{

			//初始化数据
			for(int i=0;i<15;i++){
				Integer key = 1;
				ShoppingCart shoppingCart = new ShoppingCart();
				shoppingCart.setUserId(key);
				shoppingCart.setSeq(i);
				shoppingCart.setItemCount(new Random().nextInt(100));
				shoppingCart.setLastUpdateTimestamp(new Date());
				sqlManager.insert(shoppingCart);
			}
		}

		//翻页演示
		{
			int limit = 5;
			CassandraSqlStyle.OnlyNextPageRequest onlyNextPageRequest
				= new CassandraSqlStyle.OnlyNextPageRequest(0,limit);
			String sql = "select #{page('*')} from store.shopping_cart where userid=#{userid}";
			//经过翻页调整 ，变成  select * from store.shopping_cart where userid=? and seq>? order by seq asc limit ?
			Map<String,Object> map = new HashMap<>();
			map.put("userid",1);
			PageResult<ShoppingCart> result = sqlManager.executePageQuery(sql,ShoppingCart.class,map,onlyNextPageRequest);
			System.out.println(result);
			List<ShoppingCart> shoppingCarts =  result.getList();
			if(!shoppingCarts.isEmpty()){
				ShoppingCart last = shoppingCarts.get(shoppingCarts.size()-1);
				long newSeq = last.getSeq();
				onlyNextPageRequest
					= new CassandraSqlStyle.OnlyNextPageRequest(newSeq,limit);
				//下一页
				result = sqlManager.executePageQuery(sql,ShoppingCart.class,map,onlyNextPageRequest);
				System.out.println(result);

			}
		}



    }

    public static DataSource datasource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:cassandra://127.0.0.1:9042/store?localdatacenter=datacenter1");
        ds.setDriverClassName("com.ing.data.cassandra.jdbc.CassandraDriver");
        return ds;
    }
}
