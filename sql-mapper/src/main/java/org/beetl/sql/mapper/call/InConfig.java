package org.beetl.sql.mapper.call;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 入参描述
 * <pre>
 *     call(@CallIndex(1) int age，@CallIndex(value=2,jdbcType=Types.Integer) String name);
 * </pre>
 */
@Data
public class InConfig {
	/**
	 * mapper方法中的参数和 存储过程参数的 索引
	 */
	Map<Integer,Integer> argPositionMap = new HashMap<Integer,Integer>(); ;
	/**
	 * mapper方法中参数索引 以及 响应的jdbc参数类型，可以为空
	 */
	Map<Integer,Integer> argJdbcTypeMap = new HashMap<Integer,Integer>();
	/**
	 * mapper 方法中的参数的类型
	 */
	Map<Integer,Class> argType = new  HashMap<Integer,Class>();
}
