package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;

import java.lang.annotation.Annotation;

/**
 * 提供entity生成的内置insert/update sql语句
 * @author xiandafu
 *
 */
public interface AttributePersistBuilder {
	String toSql(AbstractDBStyle dbStyle, String fieldName, String colName, Annotation an, TableDesc tableDesc);
}
