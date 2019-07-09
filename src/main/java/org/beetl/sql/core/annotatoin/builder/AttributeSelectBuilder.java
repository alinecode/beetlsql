package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.type.TypeParameter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.sql.SQLException;

/**
 * TypeParameter里对应ResultSet取出来，映射成特定对象返回
 * @author xiandafu
 *
 */
public interface AttributeSelectBuilder {
	Object toObject(SQLManager sqlManager, Annotation an, String sqlId, TypeParameter typeParameter,
			PropertyDescriptor property) throws SQLException;
}
