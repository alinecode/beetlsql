package org.beetl.sql.core;

import java.util.List;

/**
 * 批量参数
 *
 * @author liumin
 * @date 2024-01-12 14:16:02
 */
public class BatchParam {

	/**
	 * sql语句模板,优先级高于sqlId
	 * <p>
	 * 例如 update sys_user set name = #{name} where id = #{id}
	 */
	private String sqlTemplate;

	/**
	 * sql语句模板ID,由nameSpace.sqlId组成,优先级低于sqlTemplate
	 * <p>
	 * 例如 sysUser.updateByName
	 */
	private String sqlId;

	/**
	 * sql参数
	 */
	private Object sqlParam;

	private BatchParam() {
	}

	public static BatchParam builder() {
		return new BatchParam();
	}

	public BatchParam sqlTemplate(String sqlTemplate) {
		this.sqlTemplate = sqlTemplate;
		return this;
	}

	public BatchParam sqlId(String sqlId) {
		this.sqlId = sqlId;
		return this;
	}

	public BatchParam sqlParam(Object sqlParam) {
		this.sqlParam = sqlParam;
		return this;
	}

	public BatchParam sqlParamList(List<Object> sqlParam) {
		this.sqlParam = sqlParam;
		return this;
	}

	public String getSqlTemplate() {
		return sqlTemplate;
	}

	public String getSqlId() {
		return sqlId;
	}

	public Object getSqlParam() {
		return sqlParam;
	}
}
