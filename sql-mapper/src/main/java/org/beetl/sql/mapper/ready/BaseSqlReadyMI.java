package org.beetl.sql.mapper.ready;

import lombok.Data;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.SqlIdFactory;
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
	 * @param sqlManager
	 * @param c
	 * @param method
	 * @return
	 */
   public SqlId getSqId(SQLManager sqlManager,Class c , Method method){
   		if(c==null){
			return sqlManager.getSqlIdFactory().createId(method.getDeclaringClass(),method.getName());
		}else{
			return sqlManager.getSqlIdFactory().createId(c,method.getName());
		}

	}


}
