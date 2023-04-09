package org.beetl.sql.xml;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.mapping.join.AttrNode;
import org.beetl.sql.core.mapping.join.JsonConfigMapper;

import java.lang.annotation.Annotation;
import java.sql.ResultSetMetaData;
import java.util.Map;

public class XMLConfigMapper extends JsonConfigMapper {
	@Override
	protected AttrNode parse(ExecuteContext ctx, Class target, ResultSetMetaData rsmd, Annotation config) throws Exception {
		XMLMapper xmlMapperConfig = (XMLMapper)config;
		String sqlId = xmlMapperConfig.resource();
		Map<String,Object> xmlMapping = loadXMLConfig(sqlId,target,ctx.sqlManager);
		Map columnIndex = this.getColumnIndex(rsmd);
		AttrNode root =  new AttrNode(null);
		root.initNode(target,xmlMapping,columnIndex);
		return root;
	}

	protected Map<String,Object> loadXMLConfig(String sqlId,Class target, SQLManager sqlManager){
		SQLSource sqlSource  = sqlManager.getSqlLoader().querySQL(SqlId.of(sqlId));
		if(sqlSource==null){
			throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,"未能找到XMLMapper指定的配置文件 "+sqlId);
		}
		if(!(sqlSource instanceof XMLResultMapSource)){
			throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,"XMLMapper指定的配置文件 "+sqlId+" 不是映射文件");
		}

		XMLResultMapSource mapSource = (XMLResultMapSource)sqlSource;
		Map<String,Object> map = mapSource.mapConfig;
		return map;
	}
}
