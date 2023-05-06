package org.beetl.sql.core.mapping.join;

import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.sql.ResultSetMetaData;
import java.util.Map;

/**
 * 对于大多数sql映射，返回的列（metadata）是固定的，因此可以用AutoJsonMapper，它会缓存metadata和映射，AutoJsonMapper.cache
 * 很少情况下，sql查询返回的列不一样（比如通过模板sql，查询的是不同的sql），无法通过target+sqlId来缓存，因此此类用于这种情况
 */
public class AutoJsonNoCacheMapper extends AutoJsonMapper{
	@Override
	protected AttrNode parse(ExecuteContext ctx, Class target, ResultSetMetaData rsmd, Annotation config) throws Exception {
		NameConversion nc = ctx.sqlManager.getNc();
		Map columnIndex = this.getColumnIndex(rsmd);
		Map<String,Object> configMap = new CaseInsensitiveHashMap<>();
		String prefix = "";
		int level=0;
		getMappingByJson(prefix,nc,configMap,target,level);
		AttrNode root =  new AttrNode(null);
		root.initNode(target,configMap,columnIndex);
		return root;
	}
}
