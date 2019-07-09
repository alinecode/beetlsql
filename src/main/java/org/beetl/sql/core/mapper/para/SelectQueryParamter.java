package org.beetl.sql.core.mapper.para;

import org.beetl.sql.core.kit.StringKit;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;

public class SelectQueryParamter extends MapperParameter {
	Class[] paras;
	Class retType;
	// 查询中如果还想限制查询范围
	int sizePos = -1;
	int startPos = -1;

	public SelectQueryParamter(Method m, String annoParam) {
		this(m, annoParam, false);
	}

	public SelectQueryParamter(Method m, String annoParam, boolean isJdbc) {
		super(m, annoParam);
		this.m = m;
		this.paras = m.getParameterTypes();
		this.retType = m.getReturnType();
		preCheck();
	}
	protected void preCheck(){
		if(StringKit.isNotBlank(this.annoParam)){
			//总是优先通过annoParam来设置参数的名字，但jdk8以后，很少使用了
			paramsName = annoParam.split(",");
		}else{
			//主要是用方式，通过@Param或者JDK8的 parameter来获取参数的名字
			this.paramsName = checkFirst(m);
		}

		//select中的范围查找一些特殊参数，触发范围选择
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

		this.addRoot(map, array[0]);
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
