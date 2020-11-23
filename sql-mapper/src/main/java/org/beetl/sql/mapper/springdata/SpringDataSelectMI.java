package org.beetl.sql.mapper.springdata;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;
import java.util.List;

/**
 *  模拟spring data 风格，https://spring.io/projects/spring-data，支持一部分简单定义
 * <pre>{@code
 * @SpringData
 * public List<User> queryByName(String name);
 * }
 * </pre>
 *
 * 如上方法，根据方法名，会通过{@code Query} 转化成select * from user where name=name;
 */
public class SpringDataSelectMI extends MapperInvoke {

    List<SpringDataBuilder.Condition> conditions;
    SpringDataBuilder.OrderBy orderBy;
    boolean isSingle = false;
    MethodParamsHolder holder;
    public SpringDataSelectMI(List<SpringDataBuilder.Condition> conditions, MethodParamsHolder holder, boolean isSingle){
        this.conditions = conditions;

        this.holder = holder;
        this.isSingle = isSingle;
    }

    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        Query query = sm.query(entityClass);
        SpringDataBuilder.Context ctx = new  SpringDataBuilder.Context();
        ctx.query = query;
        ctx.args = args;
        ctx.target = entityClass;
        ctx.holder  = this.holder;
        for(SpringDataBuilder.Condition condition:conditions){
            condition.run(ctx);
        }
        if(isSingle){
            return ctx.query.single();
        }else{
            return ctx.query.select();
        }

    }


}
