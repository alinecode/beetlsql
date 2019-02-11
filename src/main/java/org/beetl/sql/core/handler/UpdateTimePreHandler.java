package org.beetl.sql.core.handler;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.TableDesc;

import java.lang.annotation.Annotation;

/**
 * 返回一个Beetl函数，参考
 */
public class UpdateTimePreHandler implements SQLHandler {

    @Override
    public GenValue genValue(SQLManager sqlManager, String filedName, String colName, Annotation an, TableDesc tableDesc) {
        return new BeetlScriptGenValue("date()");
    }
}
