package org.beetl.sql.core.engine.template;

import org.beetl.core.Context;

import java.util.HashMap;
import java.util.Map;

public class BeetlTemplateContext implements TemplateContext {
    Context ctx = null;
    Map local = null;
    public BeetlTemplateContext(Context ctx){
        this.ctx = ctx;
    }
    @Override
    public Object getVar(String name) {

        Object value =  ctx.getGlobal(name);
        if(value!=null){
        	return value;
		}
        if(local==null){
			/**
			 * 记录了模板里的变量位置
			 */
			Map<String, Integer> indexMap = ctx.template.program.metaData.getTemplateRootScopeIndexMap();
			Object[] values = ctx.vars;
			Map<String, Object> result = new HashMap<String, Object>(indexMap.size());
			for (Map.Entry<String, Integer> entry : indexMap.entrySet()) {
				String localName = entry.getKey();
				int index = entry.getValue();
				Object localValue = values[index];
				result.put(localName, localValue);
			}
			local = result;
		}

        value = local.get(name);
        return value;

    }
}
