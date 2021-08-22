package org.beetl.sql.mapper.template;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.builder.MethodParamsHolder;

import java.lang.reflect.Method;

/**
 * <pre>{@code
 *
 * @SqlTemplate("update user set status=#status# where id=#id#)
 * @Update
 * public int update(Integer status,Integer id);
 * }</pre>
 * @author xiandafu
 */
public class UpdateTemplateMI extends BaseTemplateMI {

    public UpdateTemplateMI(String templateSql, MethodParamsHolder holder){
        super(templateSql,holder);
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {

        return sm.executeUpdate(getSqId(entityClass,m),this.getSql(),(Object)getParas(args));
    }
}
