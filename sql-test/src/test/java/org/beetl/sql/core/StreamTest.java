package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.RowProvider;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.mapping.RowMapper;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 测试内置的查询操作
 */
public class StreamTest extends BaseTest {
    @BeforeClass
    public static void init(){
        initTable(testSqlFile);
    }

    @Test
    public void testSqlReady() throws SQLException {
        //stream操作必须再事务里使用，以期望能stream结束后，事物自动关闭数据库链接，而不是beetlsql
        DSTransactionManager.start();


        SQLReady sqlReady = new SQLReady("select * from sys_user");
        long expected = sqlManager.allCount(User.class);
        final AtomicLong total = new AtomicLong();
        StreamData<User> streamData = sqlManager.streamExecute(sqlReady,User.class);
        streamData.foreach(user -> {
            total.incrementAndGet();
        });
        Assert.assertEquals(expected,total.get());

        //测试MyRowMapper
        total.set(0);
        StreamData<MyUser> myUserStreamData = sqlManager.streamExecute(sqlReady,MyUser.class);
        myUserStreamData.foreach(user -> {
            total.incrementAndGet();
        });
        Assert.assertEquals(expected,total.get());

        //测试映射到Map
        total.set(0);
        StreamData<Map>  mapStream= sqlManager.streamExecute(sqlReady,Map.class);
        mapStream.foreach(user -> {
            total.incrementAndGet();
        });
        Assert.assertEquals(expected,total.get());

        //映射到原始类型
        total.set(0);
        SQLReady intSqlReady = new SQLReady("select id from sys_user");
        StreamData<Long> idStream = sqlManager.streamExecute(intSqlReady,Long.class);
        idStream.foreach(id -> {
            total.incrementAndGet();
        });
        Assert.assertEquals(expected,total.get());





        DSTransactionManager.commit();


    }



    @Test
    public void test() throws SQLException {
        //stream操作必须再事务里使用，以期望能stream结束后，事物自动关闭数据库链接，而不是beetlsql
        DSTransactionManager.start();

        SqlId sqlId = SqlId.of("user","streamTest");

        long expected = sqlManager.allCount(User.class);
        final AtomicLong total = new AtomicLong();
        StreamData<User> streamData = sqlManager.stream(sqlId,User.class,new HashMap<>());
        streamData.foreach(user -> {
            total.incrementAndGet();
        });
        Assert.assertEquals(expected,total.get());
        DSTransactionManager.commit();


    }

    @RowProvider(MyRowMapper.class)
    @Table(name="sys_user")
   public static class MyUser extends  User{

   }
   public static class MyRowMapper implements RowMapper{

       @Override
       public Object mapRow(ExecuteContext ctx, Object obj, ResultSet rs, int rowNum, Annotation config) throws SQLException {
           return obj;
       }
   }


}
