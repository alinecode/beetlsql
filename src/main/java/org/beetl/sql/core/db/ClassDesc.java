package org.beetl.sql.core.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.StringKit;
import org.beetl.sql.ext.gen.JavaType;

public class ClassDesc {
	Class c ;
	TableDesc  table;
	NameConversion nc;
	Set<String> propertys = new LinkedHashSet<String>();
	Set<String> dateTypes =  new LinkedHashSet<String>();;
	Set<String> cols =  new LinkedHashSet<String>();;
	String idName;
	Method idMethod = null;
	public ClassDesc(Class c,TableDesc table,NameConversion nc){
		PropertyDescriptor[] ps;
		try {
			ps = BeanKit.propertyDescriptors(c);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		for(PropertyDescriptor p:ps){
			if(p.getReadMethod()!=null&&p.getWriteMethod()!=null){
				Method readMethod = p.getReadMethod();
                String property = p.getName();
               	String col = nc.getColName(c, property);
				propertys.add(property);
				
				if(table.containCol(col)){
					cols.add(property);
				}
				
				if(col.equalsIgnoreCase(table.getIdName())){
					idName = property;
					idMethod  = readMethod;
				}
				
			
				 if( java.util.Date.class.isAssignableFrom(readMethod.getReturnType())	
					|| java.util.Calendar.class.isAssignableFrom(readMethod.getReturnType())){
					 dateTypes.add(property);
				 }
			}
			
		}
			
		
	}
	
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
		this.idName = nc.getPropertyName(table.getIdName());
		
	}
	public String getIdName(){
		return this.idName;
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
	public Method getIdMethod() {
		return idMethod;
	}
	
	
	
}
