package org.beetl.sql.ymtraix;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.kit.AutoSQLEnum;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.PostgresStyle;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 转化字段为json到数据库，以及从数据库读取json字段（字符串类型）到pojo的属性上
 * @see Jackson
 * @author xiandafu
 */
public class JacksonConvert implements AttributeConvert {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public  Object toDb(ExecuteContext ctx,  Class cls,String name, Object dbValue) {
        Object obj = BeanKit.getBeanProperty(dbValue,name);
        if(obj==null){
            return null;
        }
        try {
            String str = objectMapper.writeValueAsString(obj);
            return str;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("无法序列化对象  "+obj+" 来自于 "+dbValue.getClass()+" 的属性 "+name);
        }
    }



    @Override
    public  Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
        try{
            String json =  rs.getString(index);
            if(StringKit.isEmpty(json)){
            	return null;
			}
            PropertyDescriptor ps  = BeanKit.getPropertyDescriptor(cls,name);
            Class retType = ps.getPropertyType();
            //作为例子，这里考虑List和Map的简单泛型
            if(List.class.isAssignableFrom(retType)){
                Type type = ps.getReadMethod().getGenericReturnType();
                Class listType = BeanKit.getCollectionType(type);
                if(listType==null){
                    return objectMapper.readValue(json,List.class);
                }else{
                    return objectMapper.readValue(json,getCollectionType(List.class,listType));
                }
            } if(Map.class.isAssignableFrom(retType)){
				Type type = ps.getReadMethod().getGenericReturnType();
				Class[] mapType = BeanKit.getMapParameterTypeClass(type);
				if(mapType==null){
					return objectMapper.readValue(json,List.class);
				}else{
					return objectMapper.readValue(json,getCollectionType(List.class,mapType));
				}
			}
            else{
                return objectMapper.readValue(json,retType);
            }
        }catch(JsonParseException ex){
            throw new IllegalStateException(ex);
        }catch (JsonMappingException ex){
            throw new IllegalStateException(ex);
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }


    }

    public JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


	@Override
	public  String toAutoSqlPart(DBStyle dbStyle, Class cls,AutoSQLEnum autoSQLEnum, String name){
		if(dbStyle instanceof PostgresStyle){
			return "$$::JSON";
		}
		return null;

	}


}
