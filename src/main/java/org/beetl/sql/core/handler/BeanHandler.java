package org.beetl.sql.core.handler;

import org.beetl.sql.core.mapping.type.TypeParameter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface BeanHandler {
    public Object  toObject(Annotation an, TypeParameter typeParameter, PropertyDescriptor property) throws SQLException;
}
