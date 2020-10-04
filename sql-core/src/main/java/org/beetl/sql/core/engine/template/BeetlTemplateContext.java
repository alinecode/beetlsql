package org.beetl.sql.core.engine.template;

import org.beetl.core.Context;

 public class BeetlTemplateContext implements TemplateContext {
    Context ctx = null;
    public BeetlTemplateContext(Context ctx){
        this.ctx = ctx;
    }
    @Override
    public Object getVar(String name) {
        return ctx.getGlobal(name);
    }
}
