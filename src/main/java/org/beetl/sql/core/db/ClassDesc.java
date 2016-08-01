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
import org.beetl.sql.core.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
import org.beetl.sql.ext.gen.JavaType;

public class ClassDesc {
	Class c ;
	TableDesc  table;
	NameConversion nc;
	Set<String> propertys = new LinkedHashSet<String>();
	Set<String> dateTypes =  new LinkedHashSet<String>();;
	Set<String> cols =  new CaseInsensitiveOrderSet<String>();
	List<String> idProperties =  new ArrayList<String>(3);
	Map<String,Object> idMethods = new CaseInsensitiveHashMap<String,Object>();
	public ClassDesc(Class c,TableDesc table,NameConversion nc){
		PropertyDescriptor[] ps;
		try {
			ps = BeanKit.propertyDescriptors(c);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		Set<String> ids = table.getIdNames();

		
		for(PropertyDescriptor p:ps){
			if(p.getReadMethod()!=null&&p.getWriteMethod()!=null){
				Method readMethod = p.getReadMethod();
                String property = p.getName();
               	String col = nc.getColName(c, property);
				
				
				if(table.containCol(col)){
					col = table.getExactCol(col);
					cols.add(col);
					propertys.add(property);
				}else{
					continue ;
				}
				
				if(ids.contains(col)){
					
					idProperties.add(col);
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
	public Map<String,Object> getIdMethods() {
		return this.idMethods;
	}
	
	
	
}
