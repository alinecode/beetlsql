package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;

import java.io.StringWriter;
import java.util.Map;

/**
 * 复杂映射，申明结果集应该使用哪种映射配置
 * @author xiandafu
 * @see org.beetl.sql.core.mapping.join.JsonConfigMapper
 */
public class MappingFunction implements Function {
    private static final StringWriter STRING_WRITER = new StringWriter();
    private static final StringTemplateResourceLoader STRING_TEMPLATE_RESOURCE_LOADER =
            new StringTemplateResourceLoader();

    @Override
    public Object call(Object[] paras, Context ctx) {

        String sqlSegmentId = (String) paras[0];
        Map inputParas = ctx.globalVar;
        if (paras.length == 2) {
            Map map = (Map) paras[1];
            map.putAll(inputParas);
            inputParas = map;
        }

        ExecuteContext executeContext = (ExecuteContext)ctx.getGlobal("_executeContext");
        SqlId originalId = executeContext.sqlSource.getId();
        SqlId theId = originalId.sibling(sqlSegmentId);
        SQLSource source = executeContext.sqlManager.getSqlLoader().querySQL(theId);
        String json = "return ("+source.template+");";
        //beetl独有
        BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine) executeContext.sqlManager.getSqlTemplateEngine();
        GroupTemplate gt = beetlTemplateEngine.getBeetl().getGroupTemplate();
        Map rsMap =
                gt.runScript(
                        json, inputParas, STRING_WRITER, STRING_TEMPLATE_RESOURCE_LOADER);

        Map config = (Map)rsMap.get("return");
        executeContext.setContextPara("jsonMapping",config);
        return null;
    }


}
