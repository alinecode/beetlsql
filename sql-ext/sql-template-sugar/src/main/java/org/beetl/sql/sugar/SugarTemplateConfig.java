package org.beetl.sql.sugar;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.ext.PluginExtConfig;

/**
 用于配置sql beetl模版的语法糖，比如 ${col,asc}, 将变成${isNotEmpty(col!)?("order by "+col+" asc"):null}
 即先判断表达式中第一个变量是否有值，如果无，则不做任何输出。如果有值，则输出order by xxx asc

 */
public class SugarTemplateConfig implements PluginExtConfig {
	@Override
	public void config(SQLManager sqlManager) {
		SQLTemplateEngine sqlTemplateEngine  = sqlManager.getSqlTemplateEngine();
		if(!(sqlTemplateEngine instanceof BeetlTemplateEngine)){
			throw new IllegalStateException("不能修改SQLTemplateEngine:"+sqlTemplateEngine.getClass());
		}

		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine)sqlTemplateEngine;
		SugarSQLTemplateEngine sugarEngine = new SugarSQLTemplateEngine();
		sugarEngine.setSqlManager(sqlManager);
		beetlTemplateEngine.getBeetl().getGroupTemplate().setEngine(sugarEngine);

	}
}
