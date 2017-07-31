package org.beetl.sql.core.mapper.para;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;

import com.jfinal.template.expr.ast.Map;

public class InsertParamter extends MapperParameter {
	
	public InsertParamter(Method m,String annoParam){		
		super(m,annoParam);
		preCheck();
	}

	protected void preCheck(){
		if(this.annoParam!=null&&annoParam.length()!=0){
			paramsName = annoParam.split(",");
		}else{
			this.paramsName = checkFirst(m);
		}
		
	}
	
	
	@Override
	public Object get(Object[] array) {
		if(array==null){
			return Collections.EMPTY_MAP;
		}
		HashMap<String,Object> map = new HashMap<String,Object>();
		for(int i=0;i<array.length;i++){
			
			map.put(this.paramsName[i], array[i]);
		}
		return map;
	}
	
	
	
}
