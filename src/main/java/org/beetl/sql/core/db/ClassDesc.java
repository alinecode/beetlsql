package org.beetl.sql.core.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.ext.gen.JavaType;

public class ClassDesc {
	Class c ;
	TableDesc  table;
	NameConversion nc;
	Set<String> propertys = new LinkedHashSet<String>();
	Set<String> dateTypes =  new LinkedHashSet<String>();;
	Set<String> cols =  new LinkedHashSet<String>();;
	List<String> idProperties =  new ArrayList<String>(3);
	Map<String,Method> idMethods = new HashMap<String,Method>(3);
	public ClassDesc(Class c,TableDesc table,NameConversion nc){
		PropertyDescriptor[] ps;
		try {
			ps = BeanKit.propertyDescriptors(c);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		List<String> ids = table.getIdNames();
		idProperties =  new ArrayList<String>(ids.size());
		idMethods = new HashMap<String,Method>(ids.size());
		
		for(PropertyDescriptor p:ps){
			if(p.getReadMethod()!=null&&p.getWriteMethod()!=null){
				Method readMethod = p.getReadMethod();
                String property = p.getName();
               	String col = nc.getColName(c, property);
				propertys.add(property);
				
				if(table.containCol(col)){
					
					cols.add(property);
				}
				String tempId = col.toUpperCase();
				if(ids.contains(tempId)){
					//保持与table顺序一致
					int index = ids.indexOf(tempId);
					idProperties.add(index,col);
					idMethods.put(col,readMethod);
				}
				
				
			
				 if( java.util.Date.class.isAssignableFrom(readMethod.getReturnType())	
					|| java.util.Calendar.class.isAssignableFrom(readMethod.getReturnType())){
					 dateTypes.add(property);
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
		for(String colName:table.getMetaCols()){
			String prop = nc.getPropertyName(colName);
			this.propertys.add(prop);   
			ColDesc  colDes = table.getColDesc(colName);
			if(JavaType.isDateType(colDes.sqlType)){
				dateTypes.add(prop);
			}
			this.cols.add(prop);
		}
		for(String name:table.getIdNames()){
			this.idProperties.add(nc.getPropertyName(name));
		}
		
		
	}
	public List<String> getIdNames(){
		return this.idProperties;
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
	public Map<String,Method> getIdMethods() {
		return this.idMethods;
	}
	
	
	
}
