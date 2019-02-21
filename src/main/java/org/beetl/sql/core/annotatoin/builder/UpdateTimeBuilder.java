package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;

import java.lang.annotation.Annotation;

/**
 * 返回一个Beetl函数，参考
 */
public class UpdateTimeBuilder extends BaseAttributeBuilder {

    @Override
    public String  toSql(AbstractDBStyle dbStyle, String fieldName, String colName, Annotation an, TableDesc tableDesc){
        //#date()#,返回一个当前时间
        return this.wrapScript(dbStyle,"date()");
    }
}
