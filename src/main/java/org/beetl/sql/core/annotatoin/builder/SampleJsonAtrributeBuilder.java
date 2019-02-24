package org.beetl.sql.core.annotatoin.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.mapping.type.TypeParameter;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.SQLException;

/**
 * 一个示例，序列化,jackson(xxx),需要注册这个函数先
 */
public class SampleJsonAtrributeBuilder extends BaseAttributeBuilder {
    public static  ObjectMapper mapper = new ObjectMapper();
    public static Jackson json = new Jackson();

    @Override
    public Object  toObject(SQLManager sqlManager,Annotation an, String sqlId,TypeParameter typeParameter, PropertyDescriptor property) throws SQLException{
        if(typeParameter.getRs().wasNull()){
            return null;
        }
        String data = typeParameter.getRs().getString(typeParameter.getIndex());
        try {
            Object o = mapper.readValue(data,typeParameter.getTarget());
            return o;

        } catch (IOException e) {
            throw new SQLException("beetlsql 无法转化为json:"+data,e);
        }

    }




    @Override
    public String  toSql(AbstractDBStyle dbStyle, String fieldName, String colName, Annotation an, TableDesc tableDesc){
        return this.wrapScript(dbStyle,"jackson("+fieldName+")");
    }


    static  class Jackson implements Function{
        @Override
        public String call(Object[] paras, Context ctx) {
            Object o = paras[0];
            if(paras==null){
                return null;
            }
            try {
                return mapper.writeValueAsString(o);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("序列化失败 "+o,e);
            }
        }
    }



}
