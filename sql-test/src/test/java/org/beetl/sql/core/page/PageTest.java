package org.beetl.sql.core.page;

import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLReady;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 分页测试
 *
 */
public class PageTest extends BaseTest {

    PageRequest bigRequest = DefaultPageRequest.of(1,20);
    PageRequest smallRequest = DefaultPageRequest.of(1,10);
    @BeforeClass
    public static void init() {
        initTable("/db/page.sql");
    }



    @Test
    public void jdbcPage() {
        String sql = "select * from sys_user ";
        PageResult<User> ret =  sqlManager.execute(new SQLReady(sql),User.class,smallRequest);
        Assert.assertEquals(13,ret.getTotalRow());
        Assert.assertEquals(2,ret.getTotalPage());
        Assert.assertEquals(10,ret.getList().size());



        ret =  sqlManager.execute(new SQLReady(sql),User.class,bigRequest);
        Assert.assertEquals(13,ret.getTotalRow());
        Assert.assertEquals(1,ret.getTotalPage());
        Assert.assertEquals(13,ret.getList().size());

    }


    @Test
    public void jdbcQueryPage() {
        String sql = "select -- @pageTag(){\nid,name \n-- @}\n  from sys_user   ";
        PageResult<User> ret =  sqlManager.executePageQuery(sql,User.class,null,smallRequest);
        Assert.assertEquals(13,ret.getTotalRow());
        Assert.assertEquals(2,ret.getTotalPage());
        Assert.assertEquals(10,ret.getList().size());


        ret =  sqlManager.executePageQuery(sql,User.class,null,bigRequest);
        Assert.assertEquals(13,ret.getTotalRow());
        Assert.assertEquals(1,ret.getTotalPage());
        Assert.assertEquals(13,ret.getList().size());

    }

    /**
     * 分组查询的分页特殊
     */
    @Test
    public void groupQueryPage() {
        String sql = "select * from (select count(*) total_num,age from sys_user  group by age ) t";
        PageResult<GroupData> ret =  sqlManager.execute(new SQLReady(sql),GroupData.class,smallRequest);
        Assert.assertEquals(2,ret.getTotalRow());
        Assert.assertEquals(1,ret.getTotalPage());
        Assert.assertEquals(2,ret.getList().size());


        sql = "select #{page('*')} from (select count(*) total_num,age from sys_user  group by age ) t ";
        ret =  sqlManager.executePageQuery(sql, GroupData.class,null,smallRequest);
        Assert.assertEquals(2,ret.getTotalRow());
        Assert.assertEquals(1,ret.getTotalPage());
        Assert.assertEquals(2,ret.getList().size());




    }


    @Table(name="sys_user")
    @Data
    public static class User {
        Integer id;
        String name;
    }


    @Data
    public static class GroupData{
        int totalNum;
        int age;
    }







}
