package org.beetl.sql.mapper.ready;

import lombok.Data;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;

/**
 *
 * @author xiandafu
 */
@Data
public abstract  class BaseSqlReadyMI extends MapperInvoke {
    String sql;
    Class targetType;

	/**
	 * 使用entity+method 来生成sqlId
	 *
	 * @param c
	 * @param method
	 * @return
	 */
   public SqlId getSqId(Class c , Method method){
		return SqlId.of(c.getName(),method.getName());
	}


}
