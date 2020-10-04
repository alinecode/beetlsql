package org.beetl.sql.clazz;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.clazz.kit.CaseInsensitiveOrderSet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 找到bean定义和数据库定义共有的部分，作为实际操作的sql语句
 * @author xiandafu
 *
 */
public class ClassDesc {
	protected Class targetClass ;
	protected TableDesc  table;
	protected NameConversion nc;
	protected Set<String> properties = new CaseInsensitiveOrderSet<String>();
	//记录table和pojo的交集
	protected Set<String> cols =  new CaseInsensitiveOrderSet<String>();

	//主健
	protected List<String> idProperties =  new ArrayList<String>(3);
	protected List<String> idCols =  new ArrayList<String>(3);
	//id方法
	protected Map<String,Object> idMethods = new CaseInsensitiveHashMap<String,Object>();

	//class关注的所有注解
	protected ClassAnnotation ca = null;
	
	public ClassDesc(Class c,TableDesc table,NameConversion nc){
		this.targetClass = c ;
		ca = ClassAnnotation.getClassAnnotation(c);
		PropertyDescriptor[] ps =ca.getPropertyDescriptor(c);

		Set<String> ids = table.getIdNames();
		CaseInsensitiveHashMap<String,PropertyDescriptor> tempMap = new CaseInsensitiveHashMap<String,PropertyDescriptor>();

		for(PropertyDescriptor p:ps){
			//所有属性必须有getter和setter
			if(p.getReadMethod()!=null&& BeanKit.getWriteMethod(p, c)!=null){
				String property = p.getName();
               	String col = nc.getColName(c, property);
               	if(col!=null){
               		tempMap.put(col, p);
               	}
			}
		}
		
		
		//取交集
		for(String col :table.getCols()){
			if(tempMap.containsKey(col)){
				cols.add(col);
				PropertyDescriptor p = (PropertyDescriptor)tempMap.get(col);
				properties.add(p.getName());
				Method readMethod = p.getReadMethod();
				Class retType = readMethod.getReturnType();


				if(ids.contains(col)){
					//保持同一个顺序
					idProperties.add(p.getName());
					idCols.add(col);
					idMethods.put(p.getName(),readMethod);

				}
				
			}
		}
		
		
		
	}
	/**
	 * 用于代码生成，一个虚拟的ClassDesc，
	 * @param table
	 * @param nc
	 */
	protected ClassDesc(TableDesc table,NameConversion nc){
		this.table = table ;
		this.nc = nc ;
		for(String colName:table.getCols()){
			String prop = nc.getPropertyName(colName);
			this.properties.add(prop);
			ColDesc  colDes = table.getColDesc(colName);
			this.cols.add(colName);
		}
		for(String name:table.getIdNames()){
			this.idProperties.add(nc.getPropertyName(name));
		}
		
		
	}

	public ClassDesc buildVirtualClass(TableDesc table,NameConversion nc){
		return new ClassDesc(table,nc);
	}





	public List<String> getIdAttrs(){
		return this.idProperties;
	}

	public String getIdAttr(){
		if(this.idProperties.size()>1){
			throw new UnsupportedOperationException("不支持多主键");
		}
		return idProperties.get(0);
	}
	
	public List<String> getIdCols(){
		return idCols;
	}
	
	public Set<String>  getAttrs(){
		return properties;
	}
	

	
	public  Set<String>  getInCols(){
		return this.cols;
	}
	public Map<String,Object> getIdMethods() {
		return this.idMethods;
	}
	

	public ClassAnnotation getClassAnnoation(){
		return ca;
	}


	public Class getTargetClass() {
		return targetClass;
	}



}
