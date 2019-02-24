package org.beetl.sql.core.annotatoin.builder;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.sql.SQLException;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.mapping.type.TypeParameter;

/**
 * TypeParameter里对应ResultSet取出来，映射成特定对象返回
 * @author xiandafu
 *
 */
public interface AttributeSelectBuilder {
	 public Object  toObject(SQLManager sqlManager,Annotation an, String sqlId,TypeParameter typeParameter, PropertyDescriptor property) throws SQLException;
}
