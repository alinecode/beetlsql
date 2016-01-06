package org.beetl.sql.core.db;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.beetl.sql.core.NameConversion;
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
		Method[] ms = c.getMethods();
		for(Method m:ms){
			String name = m.getName();
			if((name.startsWith("get")|| (name.startsWith("is") && m.getReturnType().getSimpleName().equalsIgnoreCase("boolean")))
                    && m.getParameterTypes().length==0){
                String property = null;
                if(name.startsWith("get")){
                    property = StringKit.toLowerCaseFirstOne(name.substring(3));
                }else{
                    property = StringKit.toLowerCaseFirstOne(name.substring(2));
                }
				String col = nc.getColName(c, property);
				propertys.add(property);
				
				if(table.containCol(col)){
					cols.add(property);
				}
				
				if(table.getIdName().equalsIgnoreCase(col)){
					idName = property;
					idMethod  = m;
				}
				
			
				 if( java.util.Date.class.isAssignableFrom(m.getReturnType())	
					|| java.util.Calendar.class.isAssignableFrom(m.getReturnType())){
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
