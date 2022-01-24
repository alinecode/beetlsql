package org.beetl.sql.core.mapping.join;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Script;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;

import java.lang.annotation.Annotation;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 将json形式的映射规则，解析成一颗{@link AttrNode}树
 *
 * @see org.beetl.sql.annotation.entity.JsonMapper
 */
public class JsonConfigMapper extends ConfigJoinMapper {

	/**
	 * 这个是用于缓存 {@link org.beetl.sql.annotation.entity.JsonMapper} 注解中写的json规则
	 */
	protected static Map<Key, AttrNode> cache = new ConcurrentHashMap<>();
	/**
	 * 这个是用于缓存运行模板中的json规则脚本后的map
	 */
	protected static Map<SqlIdKey, AttrNode> sqlIdCache = new ConcurrentHashMap<>();

	@Override
	protected AttrNode parse(ExecuteContext ctx, Class target, ResultSetMetaData rsmd, Annotation config)
			throws Exception {
		Map<String, Object> mapping;
		AttrNode root;
		/*判断是否使用 JsonMapper 注解*/
		if (config == null) {
			mapping = (Map) ctx.getContextPara("jsonMapping");
			if (mapping == null) {
				throw new IllegalArgumentException("需要提供映射配置,@JsonMappingConfig或者在sql文件里提供'jsonMapping'变量作为配置 ");
			}
			SqlIdKey key = new SqlIdKey(target, mapping);
			root = sqlIdCache.get(key);
			if (root == null) {
				Map<String, Integer> columnIndex = super.getColumnIndex(rsmd);
				root = new AttrNode(null);
				root.initNode(target, mapping, columnIndex);
				sqlIdCache.put(key, root);
			}

		} else {
			String jsonStr = getJsonRuleStr(ctx, target, config);
			Key key = new Key(target, jsonStr);
			root = cache.get(key);
			if (root == null) {
				Map columnIndex = super.getColumnIndex(rsmd);
				mapping = getMappingByJson(ctx, jsonStr);
				root = new AttrNode(null);
				root.initNode(target, mapping, columnIndex);
				cache.put(key, root);
			}
		}

		return root;
	}

	protected String getJsonRuleStr(ExecuteContext ctx, Class target, Annotation config) {
		org.beetl.sql.annotation.entity.JsonMapper jsonMapper = (org.beetl.sql.annotation.entity.JsonMapper) config;
		String str = jsonMapper.value();
		String resource = jsonMapper.resource();
		if (str.length() != 0) {
			return str;
		} else if (resource.length() != 0) {
			SQLSource sqlSource = ctx.sqlManager.getSqlLoader().querySQL(SqlId.of(resource));
			if (sqlSource == null) {
				throw new IllegalArgumentException("未找到映射配置 " + resource);
			}
			return sqlSource.template;
		} else {
			throw new IllegalArgumentException("未提供映射配置");
		}
	}

	/**
	 * 使用Beetl把json字符串转成Map结构
	 * @param ctx
	 * @param json
	 * @return
	 */
	protected Map<String, Object> getMappingByJson(ExecuteContext ctx, String json) {
		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine) ctx.sqlManager.getSqlTemplateEngine();
		GroupTemplate gt = beetlTemplateEngine.getBeetl().getGroupTemplate();
		String scriptStr = "return (" + json + ");";

		try {
			Script script = gt.getScript(scriptStr, loader);
			script.execute();
			Map mapping = (Map) script.getReturnValue();
			return mapping;
		} catch (BeetlException beetlException) {
			throw new IllegalArgumentException("错误的mapping配置 " + beetlException.getMessage());
		}
	}

	static StringTemplateResourceLoader loader = new StringTemplateResourceLoader();

	/**
	 * 得到一个映射配置
	 * <pre>
	 *     {
	 *       "id":"id"
	 *       "userName":"user_name",
	 *       "dept":{
	 *           "id":"dept_id",
	 *           "name":"dept_name"
	 *       }
	 *       "roles":{
	 *           "code":"role_code"
	 *       }
	 *     }
	 * </pre>
	 * @param ctx
	 * @param config
	 * @return
	 */
	protected Map<String, Object> getConfig(ExecuteContext ctx, Annotation config) {
		Map<String, Object> mapping = null;
		if (config == null) {
			mapping = (Map) ctx.getContextPara("jsonMapping");
			if (mapping == null) {
				throw new IllegalArgumentException("需要提供映射配置,@JsonMappingConfig或者在sql文件里提供'jsonMapping'变量作为配置 ");
			}
			return mapping;
		}
		if (!(config instanceof org.beetl.sql.annotation.entity.JsonMapper)) {
			throw new IllegalArgumentException("配置期望是 JsonMappingConfig");
		}
		org.beetl.sql.annotation.entity.JsonMapper jsonMapper = (org.beetl.sql.annotation.entity.JsonMapper) config;
		String str = jsonMapper.value();
		String resource = jsonMapper.resource();
		if (str.length() != 0) {
			//TODO,如何把字符串转成json
			BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine) ctx.sqlManager.getSqlTemplateEngine();
			GroupTemplate gt = beetlTemplateEngine.getBeetl().getGroupTemplate();
			String scriptStr = "return " + str + ";";

			try {
				Script script = gt.getScript(scriptStr, loader);
				script.execute();
				mapping = (Map) script.getReturnValue();
				return mapping;
			} catch (BeetlException beetlException) {
				throw new IllegalArgumentException("错误的mapping配置 " + beetlException.getMessage());
			}
		} else if (resource.length() != 0) {
			throw new UnsupportedOperationException();
		} else {
			throw new IllegalArgumentException("未提供配置");
		}

	}

	static class SqlIdKey {
		Class target;
		Map mapping;

		transient int hashCode;

		public SqlIdKey(Class target, Map mapping) {
			this.target = target;
			this.mapping = mapping;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			SqlIdKey sqlIdKey = (SqlIdKey) o;
			return target.equals(sqlIdKey.target) && mapping.equals(sqlIdKey.mapping);
		}

		@Override
		public int hashCode() {
			if (hashCode == 0) {
				hashCode = Objects.hash(target, mapping);
			}
			return hashCode;
		}
	}

	static class Key {
		Class target;
		String json;

		transient int hashCode = 0;

		public Key(Class target, String json) {
			this.target = target;
			this.json = json;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Key key = (Key) o;
			return target.equals(key.target) && json.equals(key.json);
		}

		@Override
		public int hashCode() {
			if (hashCode == 0) {
				hashCode = Objects.hash(target, json);
			}
			return hashCode;
		}
	}
}
