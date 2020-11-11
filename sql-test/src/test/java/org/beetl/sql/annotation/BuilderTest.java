package org.beetl.sql.annotation;

import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.builder.*;
import org.beetl.sql.annotation.entity.Auto;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BuilderTest extends BaseTest {

    @BeforeClass
    public static void init() {
        initTable(testSqlFile);
    }

    @Test
    public void testAttrConvert() {
        UserEntity user = new UserEntity();
        user.setName("abc");
        sqlManager.insert(user);
        UserEntity userEntity = sqlManager.unique(UserEntity.class,user.getId());
        Assert.assertTrue("abc".equals(userEntity.getName()));


        SQLReady ready =  new SQLReady("select * from sys_user where id=?",user.getId());

        UserEntity userEntity1 = sqlManager.executeQueryOne(ready, UserEntity.class);
        Assert.assertTrue("abc".equals(userEntity1.getName()));

        //绕过 @Encrypt
        Map map = sqlManager.executeQueryOne(new SQLReady("select * from sys_user where id=?",user.getId()), Map.class);
        String encryptName = (String) map.get("name");
        Assert.assertTrue(encryptName.contains("-"));

    }

    @Test
    public void testBeanConvert() {
        UserEntity2 user = new UserEntity2();
        user.setName("abc");
        sqlManager.insert(user);
        UserEntity2 userEntity = sqlManager.unique(UserEntity2.class,user.getId());
        Assert.assertTrue("abc".equals(userEntity.getName()));

        SQLReady ready =  new SQLReady("select * from sys_user where id=?",user.getId());

        UserEntity2 userEntity1 = sqlManager.executeQueryOne(ready, UserEntity2.class);
        Assert.assertTrue("abc".equals(userEntity1.getName()));

        //绕过 @Encrypt
        Map map = sqlManager.executeQueryOne(new SQLReady("select * from sys_user where id=?",user.getId()), Map.class);
        String encryptName = (String) map.get("name");
        Assert.assertTrue(encryptName.contains("-"));

    }


    @Test
    public void testAddMore() {
        UserEntity3 user = new UserEntity3();
        user.setName("abc");
        sqlManager.insert(user);
        UserEntity3 entity3 = sqlManager.executeQueryOne(new SQLReady("select * from sys_user where id=?",user.getId()), UserEntity3.class);
        Date date = entity3.getCreateDate();
        Assert.assertTrue(date!=null);
    }


    @Test
    public void queryExt() {
        MyMapper myMapper = sqlManager.getMapper(MyMapper.class);
        Condition condition = new Condition().append("name", Condition.Opt.equals);
        List<UserEntity> list = myMapper.query(condition,"lijz");
        Assert.assertTrue(list.size()==1);
    }

	@Test
	public void testUpdateTime() {
		UserEntity4 userEntity4 = new UserEntity4();
		userEntity4.setName("lijzh78");

		sqlManager.insert(userEntity4);
		UserEntity4 db = sqlManager.unique(UserEntity4.class,userEntity4.getId());
		Assert.assertNotNull(db.getCreateDate());

	}






    public  interface MyMapper extends BaseMapper<UserEntity> {
        /**
         * Matcher是扩展注解，表示执行的sql，需要考虑Condition和后面的参数,这类似mybatis-plus的mapper
         * @param condition
         * @param name
         * @return
         */
        @Matcher
        List<UserEntity> query(Condition condition,String name);
    }

    @Table(name="sys_user")
    @Data
    public static class UserEntity{
        @Auto
        Long id ;
        @Encrypt()
        String name;
    }


    @Table(name="sys_user")
    @Data
    @BeanEncrypt( attr="name")
    public static class UserEntity2{
        @Auto
        Long id ;
        String name;
    }


    @Table(name="sys_user")
    @Data
    @MyUpdateTime
    public static class UserEntity3{
        @Auto
        Long id ;
        String name;
        Date createDate;
    }

	@Table(name="sys_user")
	@Data
	public static class UserEntity4{
		@Auto
		Long id ;
		String name;
		@UpdateTime
		LocalDateTime createDate;
	}


    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.METHOD, ElementType.FIELD})
    @Builder(StringConvert.class)
    public @interface Encrypt {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.TYPE})
    @Builder(UpdateContext.class)
    public @interface MyUpdateTime {

    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.TYPE})
    @Builder(BeanStringConvert.class)
    public @interface BeanEncrypt {
        String attr();
    }



    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.METHOD, ElementType.FIELD})
    @Builder(MatcherBuilder.class)
    public @interface Matcher {

    }


    public static class StringConvert implements AttributeConvert{
        public  Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
            String str =  rs.getString(index);
            return str.split("-")[0];
        }

        public  Object toDb(ExecuteContext ctx,  Class cls,String name, Object dbValue) {
            String str = (String)BeanKit.getBeanProperty(dbValue,name);
            return str+"-"+System.currentTimeMillis();

        }
    }

    public static class BeanStringConvert implements BeanConvert{
        public  Object before(ExecuteContext ctx, Object obj, Annotation an){
            BeanEncrypt beanEncrypt = (BeanEncrypt)an;
            String attrName  = beanEncrypt.attr();
            String attrValue = (String)BeanKit.getBeanProperty(obj,attrName);
            String encryptAttrValue = attrValue+"-"+System.currentTimeMillis();
            BeanKit.setBeanProperty(obj,encryptAttrValue,attrName);
            return obj;
        }
        public  Object after(ExecuteContext ctx, Object obj, Annotation an){
            BeanEncrypt beanEncrypt = (BeanEncrypt)an;
            String attrName  = beanEncrypt.attr();
            String encryptAttrValue = (String)BeanKit.getBeanProperty(obj,attrName);
            String attrValue = encryptAttrValue.split("-")[0];
            BeanKit.setBeanProperty(obj,attrValue,attrName);
            return obj;
        }
    }



    public static class UpdateContext implements TargetAdditional {

        @Override
        public Map<String, Object> getAdditional(ExecuteContext ctx, Annotation an) {
            Map map = new HashMap();
            map.put("createDate",new java.sql.Date(System.currentTimeMillis()));
            return map;
        }
    }

    public static  class  MatcherBuilder implements MapperExtBuilder {
        @Override
        public MapperInvoke parse(Class entity, Method m) {
            Class retType =  m.getReturnType();
            if(List.class.isAssignableFrom(retType)){
                //泛型，如果没有泛型，按照mapper设定，默认为mapper类设定的泛型
                Type type = m.getGenericReturnType();
                Class genericType = BeanKit.getCollectionType(type);
                return new MatcherInvoke(false,genericType==null?entity:genericType);

            }else{
                return new MatcherInvoke(true,retType);
            }
        }

        /**
         * 执行Matcher注解的方法，拼接condition 为sql并执行
         */
        public static class MatcherInvoke extends MapperInvoke {
            boolean isSingle;
            Class target;
            public MatcherInvoke(boolean isSingle,Class target){
                this.target = target;
                this.isSingle = isSingle;
            }

            @Override
            public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
                //为了简单演示期间，假设condition参数总是第一个
                Condition condition = (Condition)args[0];
                String where = condition.toSql();
                String sql = "select * from "+sm.getNc().getTableName(target)+" where "+where;
                Object[] jdbcArgs =new Object[args.length-1];
                System.arraycopy(args,1,jdbcArgs,0,jdbcArgs.length);
                List list = sm.execute(new SQLReady(sql,jdbcArgs),target);
                if(isSingle){
                    return list.isEmpty()?null:list.get(0);
                }else{
                    return list;
                }
            }
        }

    }


    public static  class Condition {
        public enum Opt {
            equals("="),large(">"),less("<");
            String token ;
            Opt(String token){
                this.token = token;
            }

            public String getToken(){
                return this.token;
            }

        }
        private TreeMap<String,Opt> map = new TreeMap<>();
        public Condition append(String name,Opt opt){
            map.put(name,opt);
            return this;
        }
        public String toSql() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Opt> entry : map.entrySet()){
                sb.append(entry.getKey()).append(entry.getValue().getToken()).append("? ").append(" and ");
            }
            sb.setLength(sb.length()-" and ".length());
            return sb.toString();
        }

    }

}