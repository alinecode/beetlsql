package org.beetl.sql.test;


import lombok.Data;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.fetch.annotation.Fetch;
import org.beetl.sql.fetch.annotation.FetchMany;
import org.beetl.sql.fetch.annotation.FetchOne;
import org.beetl.sql.sample.SampleHelper;

import java.util.List;

/**
 * 演示自动fetch,类似orm，但不同于orm，CRUD在ORM概念下过于复杂，
 * BeetlSQL的fetch没有那么多复杂概念，仅仅是加载对象后看看还有没有需要再加载的对象
 *
 *
 * @author xiandafu
 */

public class S5Fetch {

    SQLManager sqlManager;


    public S5Fetch(SQLManager  sqlManager) {
       this.sqlManager = sqlManager;
    }

    public static void main(String[] args) throws Exception {
        //为了简单起见，俩个sqlManager都来自同一个数据源，实际是不同数据库，甚至是NOSQL
        SQLManager sqlManager = SampleHelper.init();
        S5Fetch fetch = new S5Fetch(sqlManager);
        fetch.fetchOne();
        fetch.fetchMany();
    }

    /**
     *
     */
    public void fetchOne(){
        UserData user = sqlManager.unique(UserData.class,1);
        System.out.println(user.getDept());

        //fetchOne 会合并查询提高性能
        List<UserData> users = sqlManager.all(UserData.class);
        System.out.println(users.get(0).getDept());
    }

    public void fetchMany(){
        DepartmentData dept = sqlManager.unique(DepartmentData.class,1);
        System.out.println(dept.getUsers());
    }


    /**
     * fetch注解默认的level为1，即自动抓取一层，更为复杂的例子可以参考S10FetchSample
     */
    @Data
    @Table(name="sys_user")
    @Fetch
    public static class UserData {
        @Auto
        private Integer id;
        private String name;
        private Integer departmentId;
        @FetchOne("departmentId")
        private DepartmentData dept;
    }

    @Data
    @Table(name="department")
    @Fetch
    public static class DepartmentData {
        @Auto
        private Integer id;
        private String name;
        @FetchMany("departmentId")
        private List<UserData> users;
    }




}
