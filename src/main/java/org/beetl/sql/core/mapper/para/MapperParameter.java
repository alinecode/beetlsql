package org.beetl.sql.core.mapper.para;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.RowSize;
import org.beetl.sql.core.annotatoin.RowStart;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将mapper参数转化为SQLManager调用需要的Map参数，通常是mapper参数的参数名为key，但是会收到注解等配置影响
 */
public abstract class MapperParameter {
	protected String annoParam = null;
	protected Method m = null;
	protected String[] paramsName = null;
	protected boolean firstRoot = false;

	public MapperParameter(Method m){
		this.m = m;
	}

	public  MapperParameter(Method m,String annoParam){
		this.m = m;
		this.annoParam = annoParam;
	}

	/**
	 * 根据mapper参数获取sqlmanager相应方法的参数
	 * @param array
	 * @return
	 */
	public abstract Object get(Object[] array);

	protected void addRoot(Map para, Object firstPara) {
		if (this.firstRoot) {
			para.put("_root", firstPara);
		}
	}


	/**
	 * 如果配置的有annoParam
	 * @param array
	 * @return
	 * @see org.beetl.sql.core.annotatoin.SqlStatement#params
	 */
	protected  Map buildByParam(Object[] array){
		String[] names = annoParam.split(",");
		if(names.length!=array.length){
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, annoParam + " 与实际调用参数不匹配");

		}
		Map<String,Object> map = new HashMap<String,Object>();
		for (int i = 0; i < names.length; i++) {
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
		for (int i = start; i < paras.length; i++) {
			String name = getParaNameByAnnation(anns[i]);
			if(name!=null){
				//使用注解名字优先 @Param("user1") User user -> {"user1":user}
				list.add(name);
			}else{
				//使用JDK8的parameter优先  User user1 -> {"user1":user}
				list.add(paras[i].getName());
			}
		}
		return list.toArray(new String[0]);
	}


	protected String[] getGeneralName(int start){
		List<String> list = new ArrayList<String>();
		Annotation[][] anns = m.getParameterAnnotations();
		for (int i = start; i < anns.length; i++) {
			String name = getParaNameByAnnation(anns[i]);
			if(name==null){
				throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, "未命名的参数 " + start + " " + m);

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
		} else if (p instanceof Param) {
			name = ((Param)p).value();
		}else{
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER, "不支持的Sql注解 " + p + m);

		}
		return name;
	}

	protected String[] checkFirst(Method m){
		Class[] paras =m.getParameterTypes();
		String[] parameterNames = getParaName(0);
		if(paras.length>=1&&isRoot(paras[0])){
			//第一个参数考虑设置为_root,
			firstRoot = true;
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
