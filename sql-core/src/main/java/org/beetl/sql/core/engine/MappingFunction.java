package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;

import java.io.StringWriter;
import java.util.Map;

/**
 * JSON的复杂映射，申明结果集应该使用哪种映射配置。
 * <br/>
 * 使用：${jsonMapping("json-template-id")}，其中"json-template-id" 是类似写在md文件中的sql块
 * @author xiandafu
 * @see org.beetl.sql.core.mapping.join.JsonConfigMapper
 */
public class MappingFunction implements Function {

	private static final StringTemplateResourceLoader STRING_TEMPLATE_RESOURCE_LOADER = new StringTemplateResourceLoader();

	@Override
	public Object call(Object[] paras, Context ctx) {
		ExecuteContext executeContext = (ExecuteContext) ctx.getGlobal("_executeContext");
		SqlId originalId = executeContext.sqlSource.getId();
		String namespace = originalId.getNamespace();
		/*函数的第一个参数对应是书写json mapping 规则的sql resource template id*/
		String sqlSegmentId = (String) paras[0];
		if (sqlSegmentId == null || sqlSegmentId.length() == 0) {
			throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND, namespace + ".md文件下的jsonMapping存在无参数");
		}
		StringWriter stringWriter = new StringWriter();
		Map inputParas = ctx.globalVar;
		if (paras.length == 2) {
			/*第二个参数可以给规则传值，需要附合beetl规则*/
			Map map = (Map) paras[1];
			map.putAll(inputParas);
			inputParas = map;
		}
		SqlId jsonRuleId;
		if (sqlSegmentId.contains(".")) {
			/*引用其它文件的映射规则*/
			jsonRuleId = SqlId.of(sqlSegmentId);
		} else {
			/*当前文件中的其它模板片段书写的映射规则*/
			jsonRuleId = originalId.sibling(sqlSegmentId);
		}
		SQLSource source = executeContext.sqlManager.getSqlLoader().querySQL(jsonRuleId);
		/*将表示为映射规则的json字符拼接成可以运行的脚本*/
		String json = "return (" + source.template + ");";
		//beetl独有
		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine) executeContext.sqlManager.getSqlTemplateEngine();
		GroupTemplate gt = beetlTemplateEngine.getBeetl().getGroupTemplate();
		Map rsMap = gt.runScript(json, inputParas, stringWriter, STRING_TEMPLATE_RESOURCE_LOADER);

		/*json字符作为脚本执行后，返回的是Map*/
		Map config = (Map) rsMap.get("return");
		/*添加到当前执行的上下文中*/
		executeContext.setContextPara("jsonMapping", config);
		return null;
	}


}
