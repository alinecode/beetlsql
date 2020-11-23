package org.beetl.sql.ext;


import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class MappingFunction implements Function {

    private static final StringWriter STRING_WRITER = new StringWriter();
    private static final StringTemplateResourceLoader STRING_TEMPLATE_RESOURCE_LOADER =
            new StringTemplateResourceLoader();

    @Override
    public Object call(Object[] paras, Context ctx) {
        String currentSqlId = ctx.getGlobal("_id").toString();
        ExecuteContext executeContext = (ExecuteContext)ctx.getGlobal("_executeContext");


        String sqlSegmentId = (String) paras[0];
        Map inputParas = ctx.globalVar;
        if (paras.length == 2) {
            Map map = (Map) paras[1];
            map.putAll(inputParas);
            inputParas = map;
        }

        SQLManager sm = executeContext.sqlManager;
        // 保留，免得被覆盖
        List list = (List) ctx.getGlobal("_paras");
        /*获取参数指定的sqlid所在的md文件名*/
        String file = this.getParentId(ctx);
        SQLResult result;
        return null;
    }

    private String getParentId(Context ctx) {
        String id = (String) ctx.getGlobal("_id");
        int index = id.lastIndexOf(".");
        String file = id.substring(0, index);
        return file;
    }
}
