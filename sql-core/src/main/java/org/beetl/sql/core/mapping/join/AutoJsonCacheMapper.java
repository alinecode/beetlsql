package org.beetl.sql.core.mapping.join;

import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SqlId;

import java.lang.annotation.Annotation;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对于大多数sql映射，返回的列（metadata）是固定的，因此可以用AutoJsonCacheMapper，它会缓存metadata和映射，AutoJsonMapper.cache
 * 很少情况下，sql查询返回的列不一样（比如通过模板sql，查询的是不同的sql），无法通过target+sqlId来缓存，因此此类用于这种情况
 */
@Deprecated
public class AutoJsonCacheMapper extends AutoJsonMapper{
	protected static Map<AutoKey,AttrNode> cache = new ConcurrentHashMap<>();
	@Override
	protected AttrNode parse(ExecuteContext ctx, Class target, ResultSetMetaData rsmd, Annotation config) throws Exception {



		NameConversion nc = ctx.sqlManager.getNc();
		AutoKey key = new AutoKey(target,ctx.sqlId);
		AttrNode root = cache.get(key);
		if(root==null){
			Map columnIndex = this.getColumnIndex(rsmd);
			Map<String,Object> configMap = new CaseInsensitiveHashMap<>();
			String prefix = "";
			getMappingByJson(prefix,nc,configMap,target);
			root =  new AttrNode(null);
			root.initNode(target,configMap,columnIndex);
			cache.put(key,root);
		}
		return root;
	}

	static class AutoKey {
		Class target;
		SqlId sqlId;

		public AutoKey(Class target, SqlId sqlId) {
			this.target = target;
			this.sqlId = sqlId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			AutoKey autoKey = (AutoKey) o;
			return target.equals(autoKey.target) &&
				sqlId.equals(autoKey.sqlId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(target, sqlId);
		}
	}
}
