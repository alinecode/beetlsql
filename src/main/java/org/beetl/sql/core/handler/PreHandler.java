package org.beetl.sql.core.handler;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.ConstantEnum;

import java.lang.annotation.Annotation;

public interface PreHandler  {
      public GenValue genValue(SQLManager sqlManager, String filedName, String colName, Annotation an, TableDesc tableDesc);
}
