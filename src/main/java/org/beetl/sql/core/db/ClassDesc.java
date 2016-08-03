package org.beetl.sql.core.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
import org.beetl.sql.ext.gen.JavaType;

/**
 * 找到bean定义和数据库定义共有的部分，作为实际操作的sql语句
 * @author xiandafu
 *
 */
public class ClassDesc {
	Class c ;
	TableDesc  table;
	NameConversion nc;
	Set<String> propertys = new CaseInsensitiveOrderSet<String>();
	Set<String> dateTypes =  new CaseInsensitiveOrderSet<String>();
	Set<String> cols =  new CaseInsensitiveOrderSet<String>();
	List<String> idProperties =  new ArrayList<String>(3);
	List<String> idCols =  new ArrayList<String>(3);
	
	Map<String,Object> idMethods = new CaseInsensitiveHashMap<String,Object>();
	
	public ClassDesc(Class c,TableDesc table,NameConversion nc){
		this.c = c ;
		PropertyDescriptor[] ps;
		try {
			ps = BeanKit.propertyDescriptors(c);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		Set<String> ids = table.getIdNames();
		idCols.addAll(ids);
		CaseInsensitiveHashMap<String,PropertyDescriptor> tempMap = new CaseInsensitiveHashMap<String,PropertyDescriptor>();
		
		
		for(PropertyDescriptor p:ps){
			if(p.getReadMethod()!=null&&p.getWriteMethod()!=null){
				String property = p.getName();
               	String col = nc.getColName(c, property);
               	tempMap.put(col, p);
			}
		}
		
		
		
		for(String col :table.getCols()){
			if(tempMap.containsKey(col)){
				cols.add(col);
				PropertyDescriptor p = (PropertyDescriptor)tempMap.get(col);
				propertys.add(p.getName());
				if(ids.contains(col)){
					
					idProperties.add(p.getName());
					Method readMethod =  p.getReadMethod();
					Class retType = readMethod.getReturnType();
					idMethods.put(col,readMethod);
					
					
					 if( java.util.Date.class.isAssignableFrom(retType)	
								|| java.util.Calendar.class.isAssignableFrom(retType)){
								 dateTypes.add(p.getName());
							 }
				}
				
			}
		}
		
		
			
		
	}
	/**
	 * 用于代码生成，只有tabledesc
	 * @param table
	 * @param nc
	 */
	public ClassDesc(TableDesc table,NameConversion nc){
		this.table = table ;
		this.nc = nc ;
		for(String colName:table.getCols()){
			String prop = nc.getPropertyName(colName);
			this.propertys.add(prop);   
			ColDesc  colDes = table.getColDesc(colName);
			if(JavaType.isDateType(colDes.sqlType)){
				dateTypes.add(prop);
			}
			this.cols.add(colName);
		}
		for(String name:table.getIdNames()){
			this.idProperties.add(nc.getPropertyName(name));
		}
		
		
	}
	public List<String> getIdAttrs(){
		return this.idProperties;
	}
	
	public List<String> getIdCols(){
		return idCols;
	}
	
	public Set<String>  getAttrs(){
		return propertys;
	}
	
	public boolean isDateType(String property){
		return dateTypes.contains(property);
	}
	
	public  Set<String>  getInCols(){
		return this.cols;
	}
	public Map<String,Object> getIdMethods() {
		return this.idMethods;
	}
	
	
	
}
