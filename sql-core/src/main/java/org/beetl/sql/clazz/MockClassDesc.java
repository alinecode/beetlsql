package org.beetl.sql.clazz;

import java.util.Map;

/**
 * 一个通过表TableDesc获得一个ClassDesc，可以用于代码生成一个类
 */
public class MockClassDesc extends  ClassDesc {
    protected MockClassDesc(TableDesc table, NameConversion nc) {
        super(table, nc);
    }

    public Map<String,Object> getIdMethods() {
        throw new IllegalStateException("虚拟类，无此属性");
    }


    public ClassAnnotation getClassAnnotation(){
         throw new IllegalStateException("虚拟类，无此属性");
    }


    public Class getTargetClass() {
        throw new IllegalStateException("虚拟类，无此属性");
    }

}
