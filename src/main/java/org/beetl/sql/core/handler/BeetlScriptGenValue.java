package org.beetl.sql.core.handler;

public class BeetlScriptGenValue implements GenValue {
    String script;
    public BeetlScriptGenValue(String script){
        this.script = script;
    }
    @Override
    public Object get() {
        return script;
    }
}
