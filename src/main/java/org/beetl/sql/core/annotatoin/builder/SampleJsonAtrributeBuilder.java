package org.beetl.sql.core.annotatoin.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;

/**
 * 一个示例，序列化,jackson(xxx),需要注册这个函数先
 */
public class SampleJsonAtrributeBuilder extends BaseAttributeBuilder {
	public static ObjectMapper mapper = new ObjectMapper();
	public static Jackson json = new Jackson();

	static JavaType parameterizedType(Class c, Type pt) {
		if (pt instanceof ParameterizedType) {
			Type[] tv = ((ParameterizedType) pt).getActualTypeArguments();

			Class[] types = new Class[tv.length];
			for (int i = 0; i < tv.length; i++) {
				//如果还是范型
				types[i] = ((Class) tv[i]);
			}

			return getCollectionType(c, types);
		} else {
			throw new IllegalStateException(pt.toString());
		}


	}

	static JavaType getCollectionType(Class collectionClass, Class[] elementClasses) {

		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	@Override
	public Object toObject(SQLManager sqlManager, Annotation an, String sqlId, TypeParameter typeParameter,
			PropertyDescriptor property) throws SQLException {

		String data = typeParameter.getRs().getString(typeParameter.getIndex());
		if (data == null) {
			return null;
		}
		try {

			Class retType = property.getReadMethod().getReturnType();
			Type pt = property.getReadMethod().getGenericReturnType();
			//作为例子，能满足大部分场景要求了,TODO,测试更负责的情况，比如public List<Map<String,User>> getUsers();
			if (pt instanceof ParameterizedType) {
				JavaType jacksonType = parameterizedType(retType, pt);
				return mapper.readValue(data, jacksonType);
			} else {
				return mapper.readValue(data, retType);
			}


		} catch (IOException e) {
			throw new SQLException("beetlsql 无法转化为json:" + data, e);
		}

	}

	@Override
	public String toSql(AbstractDBStyle dbStyle, String fieldName, String colName, Annotation an, TableDesc tableDesc) {
		return "jackson(" + fieldName + ")";
	}

	static class Jackson implements Function {
		@Override
		public String call(Object[] paras, Context ctx) {
			Object o = paras[0];
			if (o == null) {
				return null;
			}
			try {
				return mapper.writeValueAsString(o);
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException("序列化失败 " + o, e);
			}
		}
	}


}
