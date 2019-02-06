package org.beetl.sql.core.handler;

public class BeetlScript implements GenValue {
    String script;
    public BeetlScript(String script){
        this.script = script;
    }
    @Override
    public Object get() {
        return script;
    }
}
