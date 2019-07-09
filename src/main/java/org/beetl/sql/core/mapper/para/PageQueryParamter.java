package org.beetl.sql.core.mapper.para;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.engine.PageQuery;

import java.lang.reflect.Method;


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
		if (PageQuery.class.isAssignableFrom(first)) {
			createPageQuery = false;
			if (paras.length >= 2 && this.isRoot(paras[1])) {
				this.firstRoot = true;
			}
			start = 1;
		}else if (isNumber(paras[0])&&isNumber(paras[1])){
			createPageQuery = true;
			start = 2;
			if (paras.length >= 3 && this.isRoot(paras[2])) {
				this.firstRoot = true;
			}
		}else{
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, " PageQuery查询期望参数以PageQuyer开头，或者number,number开头");			
		}
		
		if(this.annoParam!=null&&annoParam.length()!=0){
			paramsName = annoParam.split(",");
		}else{
			paramsName = getParaName(start);
		}
		
	}


	/**
	 * 是否是翻页参数的，要求int，long short，或者Number
	 * @param c
	 * @return
	 */
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
			if (pq.parasBuilder().getRoot()==null&&this.firstRoot) {
				//参数类似PageQuery query，User user,这时候user是root对象
				//前提是PageQuery没有设置root
				pq.setParas(array[1]);
			}
			for(int i=1;i<array.length;i++){
				pq.setPara(this.paramsName[i - 1], array[i]);
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
				if (pageQuery.parasBuilder().getRoot()==null&&this.firstRoot) {
					//参数类似int start,int size，User user,这时候user是root对象
					pageQuery.setParas(array[2]);
				}
				for(int i=2;i<array.length;i++){
					String name = this.paramsName[i-2];
					pageQuery.setPara(this.paramsName[i - 2], array[i]);
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
