package org.beetl.sql.core.mapper.para;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.engine.PageQuery;


public class PageQueryParamter extends MapperParameter {
	Class[] paras;
	Class retType;
	boolean isJdbc = false;
	Method m = null;
	boolean createPageQuery = false;
	public PageQueryParamter(Method m,String annoParam,boolean isJdbc){
		super(m,annoParam);
		this.m = m;
		this.paras = m.getParameterTypes();
		this.retType = m.getReturnType();;
		this.isJdbc = isJdbc;
		
		preCheck();
	}
	
	protected void preCheck(){
		int start = 0;
		Class first = paras[0];
		if(first==PageQuery.class){
			createPageQuery = false;
			start = 1;
		}else if (isNumber(paras[0])&&isNumber(paras[1])){
			createPageQuery = true;
			start = 2;
		}else{
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, " PageQuery查询期望参数以PageQuyer开头，或者number,number开头");			
		}
		
		if(this.annoParam!=null&&annoParam.length()!=0){
			paramsName = annoParam.split(",");
		}else{
			paramsName = getParaName(start);
		}
		
	}
	
	
	
	private boolean isNumber(Class c){
		if(c.isPrimitive()){
			return c==int.class||c==long.class||c==short.class;
		}else{
			return Number.class.isAssignableFrom(c);
		}
		
	}
	
	@Override
	public Object get(Object[] array) {
		if(!createPageQuery){
			PageQuery  pq = (PageQuery)array[0];
			for(int i=1;i<array.length;i++){
				String name = this.paramsName[i-1];
				if(name.equals("_root")){
					pq.setParas(array[i]);
				}else{
					pq.setPara(this.paramsName[i-1], array[i]);
				}
			}
			return pq;
		}else{
			long pageNumber = 0;
			long pageSize = 0;
			pageNumber = ((Number) array[0]).longValue();
			pageSize = ((Number) array[1]).longValue();
			PageQuery pageQuery = new PageQuery(pageNumber, pageSize);
			if(this.isJdbc){
				//其他参数在SQLReady里使用
				return pageQuery;
			}else{
				for(int i=2;i<array.length;i++){
					String name = this.paramsName[i-2];
					if(name.equals("_root")){
						pageQuery.setParas(array[i]);
					}else{
						pageQuery.setPara(this.paramsName[i-2], array[i]);
					}
				}
				return pageQuery;
			}
			
		}
	}
	
	public Object[] getJdbcArgs(Object[] args){
		
		Object[] real = new Object[args.length-2];
		System.arraycopy(args, 2, real, 0, real.length);
		return real;
	}
	public boolean isJdbc() {
		return isJdbc;
	}
	public void setJdbc(boolean isJdbc) {
		this.isJdbc = isJdbc;
	}

}
