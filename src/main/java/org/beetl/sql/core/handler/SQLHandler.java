package org.beetl.sql.core.handler;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.ConstantEnum;

import java.lang.annotation.Annotation;

/**
 * 内置生成sq语句的时候一些特殊处理
 */
public interface SQLHandler {
      public GenValue genValue(SQLManager sqlManager, String filedName, String colName, Annotation an, TableDesc tableDesc);
}
