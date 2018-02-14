package org.beetl.sql.core.mapper.para;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.RowSize;
import org.beetl.sql.core.annotatoin.RowStart;
import org.beetl.sql.core.kit.BeanKit;

public abstract class MapperParameter {
	String annoParam = null;
	Method m = null;
	String[] paramsName = null;
	public MapperParameter(Method m){
		this.m = m;
	}
	
	public  MapperParameter(Method m,String annoParam){
		this.m = m;
		this.annoParam = annoParam;
	}
	//根据mapper参数获取sqlmanager相应方法的参数1
	public abstract Object get(Object[] array);
	protected  Map buildByParam(Object[] array){
		String[] names = annoParam.split(",");
		if(names.length!=array.length){
			 throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, annoParam+" 与实际调用参数不匹配");
			
		}
		Map<String,Object> map = new HashMap<String,Object>();
		for(int i=0;i<names.length;i++){
			map.put(names[i], array[i]);
		}
		return map;
	}
	
	protected String[] getParaName(int start){
		if(JavaType.isJdk8()){
			return getJDK8ParaName( start);
		}else{
			return getGeneralName(start);
		}
	}
	
	protected String[] getJDK8ParaName(int start){
		List<String> list = new ArrayList<String>();
		Parameter[] paras = m.getParameters();
		Annotation[][] anns = m.getParameterAnnotations();
		for(int i=start;i<paras.length;i++){
			String name = getParaNameByAnnation(anns[i]);
			if(name!=null){
				list.add(name);
			}else{
				list.add(paras[i].getName());
			}
		}
		return list.toArray(new String[0]);
	}
	
	protected String[] getGeneralName(int start){
		List<String> list = new ArrayList<String>();
		Annotation[][] anns = m.getParameterAnnotations();
		for(int i=start;i<anns.length;i++){
			String name = getParaNameByAnnation(anns[i]);
			if(name==null){
				throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, "未命名的参数 "+start+" "+m);			

			}
			list.add(name);
		}
		return list.toArray(new String[0]);
	}
	
	protected String getParaNameByAnnation(Annotation[] annons){
		if(annons.length==0){
			return null;
		}
		Annotation  p = annons[0];
		String name = null;
		if(p instanceof RowStart){
			name = "_st";
		}else if(p instanceof RowSize){
			name = "_sz";
		}
		else if(p instanceof Param){
			name = ((Param)p).value();
		}else{
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, "不支持的Sql注解 "+p+m);			

		}
		return name;
	}
	
	protected String[] checkFirst(Method m){
		Class[] paras =m.getParameterTypes();
		String[] parameterNames = null;
		if(paras.length>=1&&isRoot(paras[0])){
			//第一个参数设置为root,如果没有使用@Param
			Annotation[][] anns = m.getParameterAnnotations();
			if(anns[0].length==0){
				String[] names = getParaName(1);
				String[] temp = new String[names.length+1];
				System.arraycopy(names, 0, temp,1,names.length);
				temp[0]="_root";
				parameterNames=temp;

			}else{
				parameterNames  = getParaName(0);
			}
							
		}else{
			parameterNames  = getParaName(0);
		}
		return parameterNames;
	}
	
	
	protected boolean isRoot(Class c){
		if(Map.class.isAssignableFrom(c)){
			return true;
		}
		if(c.isArray()){
			return false;
		}
		if(c.isPrimitive()){
			return false;
		}
		String name = BeanKit.getPackageName(c);
		if(name.startsWith("java.")||name.startsWith("javax.")){
			return false;
		}
		return true;
	}
}
