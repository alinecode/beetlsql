package org.beetl.sql.core.handler;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.mapping.type.TypeParameter;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 一个示例，序列化
 */
public class JsonHandler implements SQLHandler,BeanHandler {
    static ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object  toObject(Annotation an, TypeParameter typeParameter, PropertyDescriptor property) throws SQLException {
        String str = typeParameter.getRs().getString(typeParameter.getIndex());
        Class c = property.getReadMethod().getReturnType();
        try {
            return mapper.readValue(str,c);
        } catch (IOException e) {
            throw new SQLException("无法反序列化 "+str,e);
        }

    }

    @Override
    public GenValue genValue(SQLManager sqlManager, String filedName, String colName, Annotation an, TableDesc tableDesc) {
        return new BeetlScriptGenValue("json("+filedName+")");
    }
}
