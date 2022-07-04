package org.beetl.sql.mapper.call;

import lombok.Data;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.mapper.annotation.CallIndex;
import org.beetl.sql.mapper.annotation.CallOutBean;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Data
public class CallParameterParser {

	OutBeanConfig outBeanConfig = null;
	InConfig  inConfig = new InConfig()	;

	public CallParameterParser(Method method){
		parseConfig(method);
	}

	public void  parseConfig(Method method){

		InConfig  inConfig = new InConfig()	;
		Parameter[] paraConfig = method.getParameters();
		int i = 0;
		for(Parameter parameter:paraConfig){
			CallOutBean callOutBean = parameter.getAnnotation(CallOutBean.class);
			if(callOutBean!=null){
				outBeanConfig  = parseOutBeanConfig(parameter);
				outBeanConfig.setParamIndex(i);
				i++;
				continue;
			}

			CallIndex callIndex = parameter.getAnnotation(CallIndex.class);
			addCallInParameter(parameter,i,callIndex);
			i++;
		}

	};

	OutBeanConfig parseOutBeanConfig(Parameter parameter){

		Class type = parameter.getType();
		OutBeanConfig outBeanConfig = new OutBeanConfig();
		outBeanConfig.setBean(type);
		try {
			PropertyDescriptor[] allPs = BeanKit.propertyDescriptors(type);
			for(PropertyDescriptor ps:allPs){
				if(ps.getReadMethod().getDeclaringClass()==Object.class){
					continue;
				}
				CallIndex callIndexAnno = BeanKit.getAnnotation(type,ps.getName(),CallIndex.class);
				if(callIndexAnno==null){
					continue;
				}

				Integer jdbcType = callIndexAnno.jdbcType();
				Integer callOutIndex = callIndexAnno.value();
				if(callOutIndex==null){
					throw new IllegalArgumentException("需要指明索引 "+type+" 属性 "+ps.getName());
				}

				outBeanConfig.getIndexJdbcMap().put(callOutIndex,jdbcType==Integer.MAX_VALUE?null:jdbcType);
				outBeanConfig.getIndexMap().put(callOutIndex, ps.getName());
				outBeanConfig.getIndexTypeMap().put(callOutIndex,ps.getPropertyType());

			}
			return outBeanConfig;
		} catch (IntrospectionException e) {
			//不可能发生
			throw new IllegalStateException(e);
		}

	}

	void addCallInParameter(Parameter parameter,int paramIndex,CallIndex callIndexAnno){
		Integer jdbcType;
		Class type;
		Integer callInIndex;
		if(callIndexAnno==null){
			jdbcType = null;
			callInIndex = paramIndex+1;
		}else{
			jdbcType = callIndexAnno.jdbcType();
			callInIndex = callIndexAnno.value();
			if(callInIndex==null){
				callInIndex = paramIndex+1;
			}
		}

		type = parameter.getType();
		inConfig.getArgPositionMap().put(paramIndex,callInIndex);
		inConfig.getArgJdbcTypeMap().put(paramIndex,jdbcType==Integer.MAX_VALUE?null:jdbcType);
		inConfig.getArgType().put(paramIndex,type);

	}


}
