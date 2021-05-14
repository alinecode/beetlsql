package org.beetl.sql.core.mapping;

import org.beetl.sql.annotation.entity.ProviderConfig;
import org.beetl.sql.annotation.entity.RowProvider;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 一种用户完全控制的{@link ResultSet}的每行的自定义映射。 <br/>
 * 用于在 BeetlSQL 内置的映射规则基础上，用来映射额外属性。<br/>
 * 如果需要结果集映射到对象集合上，比如mybatis那种{@literal <assocation>},可以参考 {@link ResultSetMapper}
 *
 * @author xiandafu
 * @see RowProvider
 * @see ResultSetMapper
 */
public interface RowMapper<T> {

	/**
	 *
	 * @param obj 正常处理后的对象
	 * @param rs 结果集
	 * @param rowNum 处理的记录位置(第几条记录)：可以只针对某一条记录做特殊处理
	 * @param config  注解相关配置，参考 {@link ProviderConfig}
	 * @throws SQLException
	 * @return T
	 */
	T mapRow(ExecuteContext ctx, Object obj, ResultSet rs, int rowNum, Annotation config) throws SQLException;
}