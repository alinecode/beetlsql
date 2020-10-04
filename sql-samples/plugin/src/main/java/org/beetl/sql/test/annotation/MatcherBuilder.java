package org.beetl.sql.test.annotation;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.MapperBuilder;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class  MatcherBuilder implements MapperExtBuilder {
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
