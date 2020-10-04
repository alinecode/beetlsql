package org.beetl.sql.test;

import lombok.Data;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Column;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.AutoMapper;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.test.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 *  自定义注解演示
 * @author xiandafu
 * @see Jackson , json注解
 * @see LoadOne, 加载更多数据
 * @see Matcher, mapper扩展新注解
 * @see Tenant,多租户注解
 *
 */
public class PluginAnnotationSample {
    SQLManager sqlManager;

    public PluginAnnotationSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;

    }

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        PluginAnnotationSample plugin = new PluginAnnotationSample(sqlManager);
		plugin.testMatcherAnnotation();
        plugin.testJacksonAnnotation();
        plugin.loadMore();
        plugin.tenant();

    }

    /**
     * 自定义个Json注解 @Jackson，用于序列化到数据库，或者反序列化
     */
    public void testJacksonAnnotation(){
        UserInfo userInfo = new UserInfo();
        Name name = new Name();
        name.setFirstName("joel");
        name.setLastName("li");
        userInfo.setName(name);
        sqlManager.insert(userInfo);
        Integer id = userInfo.getId();
        userInfo = sqlManager.unique(UserInfo.class,id);
        System.out.println(userInfo.getName());
    }

    /**
     * 自定义一个mapper的扩展注解@Matcher，使得mapper参数中有一个Condition类，能构造查询条件。
     * 也演示了如何自定义BaseMapper和重新实现内置方法
     */
    public void testMatcherAnnotation(){
        MyMapper mapper = sqlManager.getMapper(MyMapper.class);
        {
            //@matcher注解
            List<UserDetail> list = mapper.query(new Condition().append("name", Condition.Opt.equals)
                    ,"lijz");
            System.out.println(list.size());
        }

        {
            // 重新实现的deleteById方法
            try{
                mapper.deleteById(1);
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }


    }

    /**
     * 查询数据后，在根据注解 @LoadOne定义加载更多数据
     */
    public void loadMore(){
        UserDetail userInfo = sqlManager.unique(UserDetail.class,1);
        System.out.println(userInfo.getDepartmentId());
        System.out.println(userInfo.getDept().getName());

    }


    /**
     * 查询前根据注解 @Tenant，提供一些必要的上下文参数
     */
    public void tenant(){
        //设置当前操作的租户
        TenantContext.tenantLocals.set(1);

        String sql = "select * from sys_user where department_id=#{tenantId}";
        List<TenantUser> list = sqlManager.execute(sql,TenantUser.class,new HashMap());
        System.out.println(list.get(0));

    }




    @Table(name="sys_user")
    @Data
    public static class UserInfo{
        @Column("id")
        @AutoID
        Integer id;

        @Column("name")
        @Jackson
        Name name ;
    }
    @Data
    public static class Name{
        String firstName;
        String lastName;
    }


    @Table(name="sys_user")
    @Data
    @LoadOne(name="foo")
    public static class UserDetail{
        @Column("id")
        @AutoID
        Integer id;
        String name;
        Integer departmentId;
        DepartmentInfo dept;
    }

    @Data
    @Table(name="department")
    public static class DepartmentInfo{
        @Column("id")
        @AutoID
        Integer id;
        String name;
    }


    @Data
    @Table(name="sys_user")
    @Tenant
    public static class TenantUser{
        @Auto
        private Integer id;
        @Column("name")
        private String name;
    }



    public  interface MyMapper extends  MyBaseMapper<UserInfo>{
        /**
         * Matcher是扩展注解，表示执行的sql，需要考虑Condition和后面的参数
         * @param condition
         * @param name
         * @return
         */
        @Matcher
        List<UserDetail> query(Condition condition,String  name);
    }

    /**
     * 构造一个自定的Mapper方法，继承所有BaseMapper,但deleteById会做一些特殊逻辑
     * @param <T>
     */
    public static interface  MyBaseMapper<T> extends BaseMapper{
        @AutoMapper(DeleteCheckByIdAMI.class)
        int deleteById(Object key);
    }

    public static class DeleteCheckByIdAMI extends MapperInvoke {

        @Override
        public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
            Long id = ((Number)args[0]).longValue();
        	if(id<100){
                throw new IllegalArgumentException("不允许删除 "+id);
            }
            return sm.deleteById(entityClass, args[0]);
        }

    }


}
