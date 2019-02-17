package org.beetl.sql.core.handler;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 实现特殊的转化,BeanHandler 子类应该提供一个空的构造函数，以及实现toObject或者toSql
 */
public class BeanHandler {


    /**
     * 子类继承这个方法
     * @param an
     * @param sqlId
     * @param typeParameter
     * @param property
     * @return
     * @throws SQLException
     */
    public Object  toObject(SQLManager sqlManager,Annotation an, String sqlId,TypeParameter typeParameter, PropertyDescriptor property) throws SQLException{
        return getDefaultValue(sqlManager,sqlId,typeParameter,property);
    }





    public String  toSql(AbstractDBStyle dbStyle,String fieldName, String colName, Annotation an, TableDesc tableDesc){
        return getDefaultToSql(dbStyle,fieldName);
    }

    /**
     * 默认实现
     * @param sqlId
     * @param typeParameter
     * @param property
     * @return
     * @throws SQLException
     */
    protected  Object getDefaultValue(SQLManager sqlManager,String sqlId,TypeParameter typeParameter,PropertyDescriptor property) throws SQLException{
        BeanProcessor processor = getBeanProcessor(sqlManager,sqlId);
        Map<Class, JavaSqlTypeHandler> handlers =  processor.getHandlers();
        Class propType = property.getPropertyType();
        JavaSqlTypeHandler handler = handlers.get(propType);
        if(handler==null){
            handler = processor.getDefaultHandler();
        }
        Object value = handler.getValue(typeParameter);
        return value;
    }

    private BeanProcessor getBeanProcessor(SQLManager sqlManager,String sqlId) {
        //这个代码与SQLScript代码重复
        BeanProcessor bp = sqlManager.getProcessors().get(sqlId);
        if (bp != null) {
            return bp;
        }
        String ns = sqlId.substring(0, sqlId.indexOf("."));
        bp = sqlManager.getProcessors().get(ns);
        if (bp != null) {
            return bp;
        } else {
            return sqlManager.getDefaultBeanProcessors();
        }



    }

    protected String getDefaultToSql(AbstractDBStyle dbStyle,String fieldName){
        // #filedName#
        return wrapScript(dbStyle,fieldName);
    }

    protected String wrapScript(AbstractDBStyle style,String sqlScript){
        String start = style.HOLDER_START;
        String end = style.HOLDER_END;
        StringBuilder sb = new StringBuilder(sqlScript.length()+start.length()+end.length());
        sb.append(start).append(sqlScript).append(end);
        return sb.toString();
    }

}
