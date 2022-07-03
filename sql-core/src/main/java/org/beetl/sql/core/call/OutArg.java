package org.beetl.sql.core.call;

import lombok.Data;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;

/**
 * 存储过程出参
 */
@Data
public class OutArg extends CallArg {
	private Class outType;
	//输出结果
	private Object outValue;

	/**
	 *
	 * @param outType  出参的java类型, jdbcType 根据BeanProcessor的handler来判断
	 * @see JavaSqlTypeHandler#jdbcType()
	 */
	public OutArg(Class outType) {
		this.outType = outType;
	}

	public OutArg(Class outType,int jdbcType) {
		this.outType = outType;
		this.jdbcType = jdbcType;
	}

}
