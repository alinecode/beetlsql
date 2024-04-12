package org.beetl.sql.mapper.internal;

import org.beetl.core.util.ArrayUtils;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.ListUtil;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 根据主键获取某条记录的某个具体值
 *<pre>@{code
 *UserDao dao = sqlManager.getMapper(UserDao.class);
 *Integer dept = (Integer)dao.getProperty(1,User::getDepartmentId);
 *System.out.println(dept);
 *}</pre>
 * @author lijiazhi
 */
public class GetFieldsByIdAMI extends MapperInvoke {

    @Override
    @SuppressWarnings("unchecked")
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {

        Object key = args[0];
        LambdaQuery.Property<Object, ?>  property = (LambdaQuery.Property<Object, ?>)args[1];

        LambdaQuery<Object> lambdaQuery = sm.lambdaQuery(entityClass);
		TableDesc tableDesc = sm.getTableDesc(sm.getNc().getTableName(entityClass));
		if(tableDesc.getIdNames().size()>1){
			throw new BeetlSQLException(BeetlSQLException.MAPPER_ERROR,"只支持一个主键，但表定义了" +tableDesc.getIdNames());
		}
		String colName = lambdaQuery.getColumnName(property);
		Class type =lambdaQuery.getColumnType(property);

		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(colName).append(" from ").append(tableDesc.getName());
		sb.append(" where ").append(tableDesc.getIdNames().toArray()[0]).append("=?");
		SQLReady sqlReady = new SQLReady(sb.toString(), key);
		Object ret = sm.executeQueryOne(sqlReady,type);
        return ret;
    }


}
