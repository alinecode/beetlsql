package org.beetl.sql.core.annotatoin.builder;

import java.lang.annotation.Annotation;

import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;

/**
 * 提供entity生成的内置insert/update sql语句
 * @author xiandafu
 *
 */
public interface AttributePersistBuilder {
	  public String  toSql(AbstractDBStyle dbStyle,String fieldName, String colName, Annotation an, TableDesc tableDesc);
}
