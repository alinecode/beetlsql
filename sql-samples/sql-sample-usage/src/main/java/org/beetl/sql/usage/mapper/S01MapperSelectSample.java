package org.beetl.sql.usage.mapper;


import lombok.Data;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.usage.mapper.dao.UserSelectMapper;

import java.util.Arrays;
import java.util.List;

/**
 * 新增，修改和删除都是update
 */
public class S01MapperSelectSample {
    UserSelectMapper mapper = null;
    public S01MapperSelectSample(UserSelectMapper mapper){
        this.mapper = mapper;
    }
    public static void  main(String[] args){
        SQLManager sqlManager = SampleHelper.getSqlManager();
        UserSelectMapper mapper = sqlManager.getMapper(UserSelectMapper.class);
        S01MapperSelectSample sample = new S01MapperSelectSample(mapper);
         /*内置操作*/
        sample.selectById();
        sample.selectByIds();
        sample.all();
        sample.template();
        sample.execute();
        sample.exist();


         /*用户提供sql*/
        sample.sqlAnnotation();
        sample.templateAnnotation();
        sample.springDataStyle();
        sample.defaultMethod();
        sample.anyPojo();
        sample.queryBySqlProvider();
        sample.queryBySqlTemplateProvider();
        sample.sqlIdSelect();



    }

    /**
     *  根据主键查询
     */
    public void selectById(){
       Integer id= 1;
       UserEntity userEntity = mapper.unique(1);
        //single方法，如果未找到，返回null，不会抛出BeetlSQLException.UNIQUE_EXCEPT_ERROR
        UserEntity userEntity2 = mapper.single(Integer.MAX_VALUE);

    }

    /**
     * 返回多个对象
     */
    public void selectByIds(){
        List<Integer> ids = Arrays.asList(1,2);
        List<UserEntity> list = mapper.selectByIds(ids);
        System.out.println(list.size());

    }

    /**
     * 查询所有对象，比如一些小表全部加载
     */
    public void all(){
        long count =  mapper.allCount();
        if(count<10000){
            List<UserEntity> list = mapper.all();
            System.out.println(list.size());
        }

    }

    /**
     * 是否存在
     */
    public void exist(){
        System.out.println(mapper.exist(1));
    }


    /**
     * 按照提供的对象查询
     */
    public void template(){
        //加载所有部门id为1的
        UserEntity entity = new UserEntity();
        entity.setDepartmentId(1);
        List<UserEntity> list = mapper.template(entity);
        /**
         * 当确认只有一个返回值，可以使用templateOne,比如用户名和密码只返回一个或者返回null(用户名和密码错)
         * 但是如果你使用templateOne，但可能返回多个值，那则只能处理第一个值，有可能错过处理其他值
         */

        UserEntity entity2 = new UserEntity();
        entity2.setId(1);
        UserEntity ret = mapper.templateOne(entity2);
        System.out.println(ret.getId()+":"+ret.getName());

    }

    /**
     * 直接执行jdbc sql
     */
    public void execute(){
        List<UserEntity> users = mapper.execute("select * from sys_user where name=?","ok2");
        System.out.println(users.size());
    }

    /**
     * 使用Query类，关于Query类更多例子，参考QuerySample
     */
    public void query(){
        List<UserEntity> users = mapper.createLambdaQuery()
                .andEq(UserEntity::getName,"abc").select();
        System.out.println(users.size());
    }

    /**
     * 使用@Sql注解提供sql
     */
    public void sqlAnnotation(){
        List<UserEntity> list =mapper.select("ok2");

        UserEntity userEntity = mapper.selectSingleOne(1);
    }

    /**
     * 使用@Tempalte 提供sql模板
     */
    public void templateAnnotation(){
        List<UserEntity> list =mapper.select2("ok2");
    }

    /**
     * 使用SpringData风格，通过方法名提供sql
     */
    public void springDataStyle(){
        List<UserEntity> list =mapper.queryByNameAndDepartmentId("lijz",1);
    }

    /**
     * 使用default method完成复杂逻辑处理
     */
    public void defaultMethod(){
        UserEntity userEntity =mapper.selectById(1);
    }

    /**
     * 查询返回结果可以是任何类型实体
     */
    public void anyPojo(){
        List<UserInfo> users =  mapper.anyPojo();

    }

    /**
     * queryUserByCondition方法使用注解@SqlProvider提供一个sql
     */
    public void queryBySqlProvider(){
        List<UserEntity> list = mapper.queryUserByCondition("abc");
    }

    public void queryBySqlTemplateProvider(){
        List<UserEntity> list = mapper.queryUserByTemplateCondition("abc");
    }

    public void sqlIdSelect(){
        List<UserEntity> list = mapper.selectByUserName("abc");
    }




    public static class  SelectUserProvider{
        /*与mapper方法同名，同参数 */
        public SQLReady queryUserByCondition(String name){
            SQLReady ready = null;
            if(name==null){
                String sql =  "select * from sys_user where 1=1";
                ready = new SQLReady(sql);
            }else{
                String sql =  "select * from sys_user where name=?";
                ready = new SQLReady(sql,name);
            }
            return ready;
        }
        /*与mapper方法同名*/
        public String queryUserByTemplateCondition(String name){
            String sql = "select * from sys_user where 1=1";
            if(name==null){
                return sql;
            }else{
                return sql+" and name=#{name}";
            }
        }
    }

    @Data
    public static class UserInfo{
        String name;
        Integer deptId;
    }



}
