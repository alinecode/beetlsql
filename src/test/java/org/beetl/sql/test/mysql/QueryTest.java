package org.beetl.sql.test.mysql;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.test.Blog;
import org.beetl.sql.test.BlogDao;
import org.beetl.sql.test.MysqlDBConfig;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

public class QueryTest {

    public static void main(String[] args) throws Exception {
        String javaVersion = System.getProperty("java.version");
        System.out.println(javaVersion);
        MySqlStyle style = new MySqlStyle();
        ConnectionSource cs = ConnectionSourceHelper.getSingle(datasource());
        SQLLoader loader = new ClasspathLoader("/org/beetl/sql/test");
        DebugInterceptor debug = new DebugInterceptor();

        Interceptor[] inters = new Interceptor[]{debug};
        final SQLManager sql = new SQLManager(style, loader, cs, new UnderlinedNameConversion(), inters);
        //预先注册一个，否则没有办法使用@Jackson注解
//        sql.getBeetl().getGroupTemplate().registerFunction("jackson", JsonHandler.json);
        BlogDao dao = sql.getMapper(BlogDao.class);
        select(dao);

    }

    public static void select(BlogDao dao) {
        LambdaQuery<Blog> query = dao.createLambdaQuery();
        Blog blog = query.andEq(Blog::getTitle, Query.filterNull(null))
                .andIn(Blog::getId, Arrays.asList(1,2,3,4,5,6,7))
                .andNotIn(Blog::getId, Query.filterEmpty(Collections.EMPTY_LIST))
                .andNotEq(Blog::getId, Query.filterEmpty(""))
                .andLess(Blog::getId, Query.filterEmpty(2))
                .andGreatEq(Blog::getId, Query.filterEmpty(0))
                .singleSimple();
        System.out.println(blog.getTitle() + "----------");
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
}
