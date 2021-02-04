package org.beetl.sql.clazz;

/**
 * 定义了sql类型，这里的类型是从调用sqlManagerAPI里得出来的，并不是真的分析了sql语句
 */
public enum SQLType {
    INSERT,
    SELECT,
    /*一些非内置的sql，sqlManager只提供update的api，可能是delete语句，但调用了update*/
    UPDATE,
    DELETE,
    UNKNOWN;

    public boolean isUpdate(){
        return this==INSERT||this==UPDATE||this==DELETE;
    }

}
