package org.beetl.sql.test;


import lombok.Data;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.TargetSQLManager;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.sample.SampleHelper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <ui>
 *     <li>
 *      演示多数据源操作中的ConditionalSQLManager，按照条件决定使用哪个SQLManager
 *      ConditionalSQLManager.decide方法决定使用哪个SQLManager，
 *      decide默认会读取目标对象的TargetSQLManager注解来决定，SQLManager的有些api参数没有目标对象，则使用默认SQLManager
 *     </li>
 *     <li>
 *         演示user分表操作，动态表名实现分表
 *     </li>
 *     <li>
 *         演示user分库操作，根据条件决定数据访问哪个数据库，使用了{@link ConditionalConnectionSource}
 *     </li>
 * </ui>

 *
 *
 * 注意：分库分表最好使用中间件
 * @author xiandafu
 */

public class S6MoreDatabase {

    public S6MoreDatabase() {
    }

    public static void main(String[] args) throws Exception {

        S6MoreDatabase moreSource = new S6MoreDatabase();
        moreSource.conditional();
        moreSource.masterSlave();
        moreSource.multipleTables();
        moreSource.multipleDataBaseAndTables();
    }

    /**
     * 多数据源协作
     */
    public void conditional() {

        SQLManager a = SampleHelper.init();
        SQLManager b = SampleHelper.init();
        Map<String, SQLManager> map = new HashMap<>();
        map.put("a", a);
        map.put("b", b);
        SQLManager sqlManager = new ConditionalSQLManager(a, map);

        //不同用户，用不同sqlManager操作，存入不同的数据库
        UserData user = new UserData();
        user.setName("hello");
        user.setDepartmentId(2);
        sqlManager.insert(user);

        DepartmentData dept = new DepartmentData();
        dept.setName("dept");
        sqlManager.insert(dept);
    }


    /**
     * 普通一主多从
     */
    public void masterSlave(){
        //为了简单起见，主从库都走同一个数据库
        DataSource master = SampleHelper.datasource();
        DataSource slave1 = SampleHelper.datasource();
        DataSource slave2 = SampleHelper.datasource();
        ConnectionSource source = ConnectionSourceHelper.getMasterSlave(master,new DataSource[]{slave1,slave2});

        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MySqlStyle());
        SQLManager sqlManager = builder.build();

        //更新操作走主库
        UserData user = new UserData();
        user.setName("a");
        user.setDepartmentId(1);
        sqlManager.insert(user);
        //查询走从库
        sqlManager.unique(UserData.class,1);


    }

    /**
     * 单库分表操作,user对象的{@code @Table}注解是逻辑表达式
     * <pre>{@code
     *     @Table(name="${toTable('user',id)}"
     *     public class User{
     *
     *     }
     * }</pre>
     * toTable方法是一个自定义注册的beetl方法，在运行的时候会根据id换算出真实表
     *
     * 对于beetlsql所有内置方法，都可以自动分表，但你自己的sql，也要类似使用
     * {@code ${toTable('user',id)}}
     * @see TableChoice
     */
    public void multipleTables(){

        SQLManager sqlManager = getSQLManager4MultipleTables();
        //使用user表
        sqlManager.deleteById(MyUser.class,199);
        MyUser user = new MyUser();
        user.setName("abc");
        user.setId(199);
        sqlManager.insert(user);

        //使用user_1表. 为了简单起见，分表逻辑返回的目标表还是user表
        MyUser user2 = new MyUser();
        user2.setName("abc");
        user2.setId(1500);
        sqlManager.insert(user2);

    }

    /**
     * 分库分布表操作，同{@link #multipleTables()} 方法，但增加如果id超过一定限额，走另外一个数据库
     * 核心还是需要定义一个分库分表逻辑
     * @see TableAndDataBaseChoice
     */
    public void multipleDataBaseAndTables(){

        SQLManager sqlManager = getSQLManager4MultipleDatBase();
        sqlManager.deleteById(MyUser.class,199);
        MyUser user = new MyUser();
        user.setName("abc");
        user.setId(199);
        sqlManager.insert(user);

        //这条记录使用第二个库的user表
        sqlManager.deleteById(MyUser.class,2900);
        MyUser user2 = new MyUser();
        user2.setName("abc");
        user2.setId(2900);
        sqlManager.insert(user2);

    }

    protected SQLManager getSQLManager4MultipleTables(){
        SQLManager sqlManager = SampleHelper.getSqlManager();
        //告诉sqlManager遇到USER_TABLE这个不存在的表不慌，他是个虚表，真实表是user
        sqlManager.addVirtualTable("sys_user",USER_TABLE);
        BeetlTemplateEngine templateEngine = (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
        // 注册一个方法来实现映射到多表的逻辑
        templateEngine.getBeetl().getGroupTemplate().registerFunction("toTable", new Function(){

            @Override
            public Object call(Object[] paras, Context ctx) {
                String tableName = (String)paras[0];
                Integer id = (Integer)paras[1];
                //使用分表逻辑
                TableChoice tableChoice = new TableChoice();
                return tableChoice.getTableName(tableName,id);

            }
        });
        return sqlManager;
    }

    /**
     * 分表选择逻辑
     */
    public static  class TableChoice{
        public String getTableName(String tableName,Integer id){
            if(id<1000){
                return tableName;
            }else{
                //根据需要返回另外一个表，比如tableName+"_1"
                return tableName;
//                return tableName+"_1";
            }
        }
    }

    /**
     * 分库选择逻辑，用户自由实现分表分库逻辑，
     */
    public static  class TableAndDataBaseChoice{
        public String getTableName(ExecuteContext executeContext,String tableName,Integer id){

            if(id<1000){
                return tableName;
            }else if(id<2000){
                return tableName+"_1";
            }else{
                //如果继续大，设置一个标记，进入另外一个数据库cs2库的user表

                executeContext.setContextPara(FLAG,"cs2");
                if(id<3000){
                    return tableName;
                }else{
                    return  tableName+"_1";
                }

            }

        }
    }



    private static final String FLAG ="connectionSource";
    protected  SQLManager getSQLManager4MultipleDatBase(){
        //为了测试方便，假设指向同一个数据库
        DataSource db1 = SampleHelper.datasource();
        ConnectionSource cs1 = ConnectionSourceHelper.getSingle(db1);
        DataSource  db2 = SampleHelper.datasource();
        ConnectionSource cs2 = ConnectionSourceHelper.getSingle(db2);
        Map<String,ConnectionSource>  datas = new HashMap<>();
        datas.put("cs1",cs1);
        datas.put("cs2",cs2);
        // 配置策略
        ConditionalConnectionSource.Policy policy = new ConditionalConnectionSource.Policy() {
            @Override
            public String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate) {
                String name = (String)ctx.getContextPara(FLAG);
                if(name!=null){
                    return name;
                }else{
                    // 如果没有设置，则返回一个默认库
                    return "cs1";
                }
            }

            @Override
            public String getMasterName() {
                return "cs1";
            }
        };


       ConditionalConnectionSource ds  = new ConditionalConnectionSource(policy,datas);
        // 初始化sqlManager，使用ConditionalConnectionSource
        SQLManagerBuilder builder = new SQLManagerBuilder(ds);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        builder.setDbStyle(new MySqlStyle());
        SQLManager sqlManager = builder.build();
        // 申明一个虚表 "${toTable('user',id)}"，实际上是user表
        sqlManager.addVirtualTable("sys_user",USER_TABLE);
        BeetlTemplateEngine templateEngine = (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
        // 注册一个方法来实现映射到多表的逻辑
        templateEngine.getBeetl().getGroupTemplate().registerFunction("toTable", new Function(){

            @Override
            public Object call(Object[] paras, Context ctx) {
                String tableName = (String)paras[0];
                Integer id = (Integer)paras[1];
                ExecuteContext executeContext = (ExecuteContext)ctx.getGlobal(ExecuteContext.NAME);
                //使用分库逻辑
                TableAndDataBaseChoice choice = new TableAndDataBaseChoice();
                return choice.getTableName(executeContext,tableName,id);

            }
        });

        return sqlManager;

    }








    /**
     * 用户数据使用"a" sqlmanager
     */
    @Data
    @Table(name = "sys_user")
    @TargetSQLManager("a")
    public static class UserData {
        @Auto
        private Integer id;
        private String name;
        private Integer departmentId;
    }

    /**
     * 部门数据使用"b" sqlmanager
     */
    @Data
    @Table(name = "department")
    @TargetSQLManager("b")
    public static class DepartmentData {
        @Auto
        private Integer id;
        private String name;
    }


    static final String USER_TABLE="${toTable('sys_user',id)}";

	/**
	 *
	 * @see org.beetl.sql.core.meta.MetadataManager#addTableVirtual(String, String)
	 */
	@Data
    @Table(name = USER_TABLE)
    public static class MyUser {
        @AssignID
        private Integer id;
        private String name;
    }


}
