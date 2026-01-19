package org.beetl.sql.fetch;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

class ParameterTypUtil {
	/*TODO,与ReturnTypeParser 代码重复*/
	public static Class getCollectionType(Type type){
		if(!(type instanceof ParameterizedType) ){
			throw new IllegalStateException("无泛型类型，无法Fetch");
		}
		Class paraType =  getParamterTypeClass(type);
		if(paraType==null){
			throw new IllegalStateException("无泛型类型，无法Fetch");
		}
		return paraType;
	}

	public static  Class getParamterTypeClass(Type t) {
		if (t instanceof WildcardType || t instanceof TypeVariable) {
			// 丢失类型
			return null;
		} else if (t instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
		} else {
			throw new UnsupportedOperationException();
		}

	}
}
