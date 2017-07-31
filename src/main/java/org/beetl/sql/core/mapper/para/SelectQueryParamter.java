package org.beetl.sql.core.mapper.para;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;

import com.jfinal.template.expr.ast.Map;

public class SelectQueryParamter extends MapperParameter {
	Class[] paras;
	Class retType;
	int sizePos = -1;
	int startPos = -1;
	public SelectQueryParamter(Method m,String annoParam){		
		this(m,annoParam,false);
	}
	public SelectQueryParamter(Method m,String annoParam,boolean isJdbc){		
		super(m,annoParam);
		this.m = m;
		this.paras = m.getParameterTypes();
		this.retType = m.getReturnType();
		preCheck();
	}
	protected void preCheck(){
		if(this.annoParam!=null&&annoParam.length()!=0){
			paramsName = annoParam.split(",");
		}else{
			this.paramsName = checkFirst(m);
		
		}
		for(int i=0;i<paramsName.length;i++){
			String name = paramsName[i];
			if(name.equals("_st")){
				this.startPos = i;
			}else if(name.equals("_sz")){
				this.sizePos = i;
			}
			
		}
		
	}
	
	
	
	@Override
	public Object get(Object[] array) {
		if(array==null){
			return Collections.EMPTY_MAP;
		}
		HashMap<String,Object> map = new HashMap<String,Object>();
		for(int i=0;i<array.length;i++){
			if(i==this.sizePos||i==this.startPos){
				//不作为sqlManagaer参数
				continue;
			}
			map.put(this.paramsName[i], array[i]);
		}
		return map;
	}
	
	public boolean hasRangeSelect(){
		return this.sizePos!=-1;
	}
	public int getSizePos() {
		return sizePos;
	}
	public void setSizePos(int sizePos) {
		this.sizePos = sizePos;
	}
	public int getStartPos() {
		return startPos;
	}
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	
	
}
