
package org.beetl.sql.mapper.internal;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.template.BaseTemplateMI;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <pre>{@code
 *
 * @SqlTemplate("update user set status=#status# where id=#id#)
 * @Tempalte
 * public int[] updateTemplate(List<User> user);
 * }</pre>
 * @author zj
 */
public class UpdateTemplateByIdBatchAMI extends MapperInvoke {
	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		if(args.length!=1){
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER,"期望包含一个List集合");
		}
		Object para = args[0];
		if(!(para instanceof  List)){
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER,"期望包含一个List集合");
		}
		return sm.updateBatchTemplateById(entityClass, (List)para);
	}
}
