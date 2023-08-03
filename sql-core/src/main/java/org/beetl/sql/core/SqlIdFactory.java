package org.beetl.sql.core;

import org.beetl.sql.clazz.kit.AutoSQLEnum;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.clazz.kit.StringKit;


/**
 * 得到一个SqlId，可以自定义
 *
 */
@Plugin
public class SqlIdFactory {

	static class Template {

	}

	static class Sql {

	}

	public SqlId buildIdentity(Class entity, AutoSQLEnum autoSQLEnum) {
		SqlId sqlId = createId(entity, autoSQLEnum.getClassSQL());
		sqlId.managedType = SqlId.ManagedType.auto;
		return sqlId;
	}


	public SqlId createId(Class entity, String id) {
		String className = getNamespace(entity);
		SqlId sqlId = SqlId.of(className, id);
		sqlId.managedType = SqlId.ManagedType.resource;
		return sqlId;
	}

	protected String getNamespace(Class entity) {
		return StringKit.toLowerCaseFirstOne(entity.getName());
	}


	public SqlId buildTemplate(String template) {
		SqlId id = createId(Template.class, template);
		id.managedType = SqlId.ManagedType.template;
		return id;
	}

	public SqlId buildSql(String sql) {
		SqlId id = createId(Sql.class, sql);
		id.managedType = SqlId.ManagedType.sql;
		return id;
	}


}
