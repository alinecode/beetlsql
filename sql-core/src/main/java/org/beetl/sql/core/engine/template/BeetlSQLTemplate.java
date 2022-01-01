package org.beetl.sql.core.engine.template;

import org.beetl.core.Template;
import org.beetl.core.io.SoftReferenceWriter;
import org.beetl.sql.clazz.kit.Plugin;

import java.util.Map;

/**
 * 用于解释Beetl模板
 * @author xiandafu
 */
@Plugin
 class BeetlSQLTemplate implements  SQLTemplate {

    Template template;
    public BeetlSQLTemplate(Template template){
        this.template = template;
    }
    @Override
    public void setPara(String name, Object value) {
        template.binding(name,value);
    }

    @Override
    public void setParas(Map map) {
        template.binding(map);
    }

    @Override
    public String render() {
    	String sql = template.render();
		return sql;
    }

    @Override
    public TemplateContext getContext() {
        return new BeetlTemplateContext(template.getCtx());
    }

}
