package org.beetl.sql.core.handler;

public class  RawGenValue  implements  GenValue {
    Object value;
    public RawGenValue(Object value){
        this.value = value;
    }

    @Override
    public Object get() {
        return value;
    }
}
