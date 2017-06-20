package org.beetl.sql.core.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.annotatoin.ColumnIgnore;
import org.beetl.sql.core.annotatoin.InsertIgnore;
import org.beetl.sql.core.annotatoin.UpdateIgnore;
import org.beetl.sql.core.annotatoin.Version;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;

/**
 * 找到bean定义和数据库定义共有的部分，作为实际操作的sql语句
 * @author xiandafu
 *
 */
public class ClassDesc {
	Class targetClass ;
	TableDesc  table;
	NameConversion nc;
	Set<String> propertys = new CaseInsensitiveOrderSet<String>();
	Set<String> dateTypes =  new CaseInsensitiveOrderSet<String>();
	Set<String> cols =  new CaseInsensitiveOrderSet<String>();
	List<String> idProperties =  new ArrayList<String>(3);
	List<String> idCols =  new ArrayList<String>(3);
	Map<String,ColumnIgnoreStatus> attrIgnores = new HashMap<String,ColumnIgnoreStatus>();
	Map<String,Object> idMethods = new CaseInsensitiveHashMap<String,Object>();
	String ormQuery = null;
	String versionProperty;
	String versionCol;
	
	public ClassDesc(Class c,TableDesc table,NameConversion nc){
		this.targetClass = c ;
		PropertyDescriptor[] ps;
		try {
			ps = BeanKit.propertyDescriptors(c);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		Set<String> ids = table.getIdNames();
//		idCols.addAll(ids);
		CaseInsensitiveHashMap<String,PropertyDescriptor> tempMap = new CaseInsensitiveHashMap<String,PropertyDescriptor>();
		
		
		for(PropertyDescriptor p:ps){
			
			if(p.getReadMethod()!=null&&BeanKit.getWriteMethod(p, c)!=null){
				String property = p.getName();
               	String col = nc.getColName(c, property);
               	if(col!=null){
               		tempMap.put(col, p);
               	}
			}
		}
		
		
		
		for(String col :table.getCols()){
			if(tempMap.containsKey(col)){
				cols.add(col);
				PropertyDescriptor p = (PropertyDescriptor)tempMap.get(col);
				propertys.add(p.getName());
				Method readMethod =  p.getReadMethod();
				ColumnIgnore sqlIgnore = BeanKit.getAnnoation(c, p.getName(), readMethod, ColumnIgnore.class);
				if(sqlIgnore!=null){
					attrIgnores.put(p.getName(), new ColumnIgnoreStatus(sqlIgnore));
				}else{
					//2.8.13 后新增
					InsertIgnore ig = BeanKit.getAnnoation(c, p.getName(), readMethod, InsertIgnore.class);
					UpdateIgnore ug = BeanKit.getAnnoation(c, p.getName(), readMethod, UpdateIgnore.class);
					if(ig!=null||ug!=null){
						attrIgnores.put(p.getName(), new ColumnIgnoreStatus(ig,ug));
					}
				}
				
				Version version =  BeanKit.getAnnoation(c, p.getName(), readMethod, Version.class);
				if(version!=null){
					this.versionProperty = p.getName();
					this.versionCol = col;
				}
				if(ids.contains(col)){
					//保持同一个顺序
					idProperties.add(p.getName());
					idCols.add(col);
					Class retType = readMethod.getReturnType();
					idMethods.put(p.getName(),readMethod);
					
					
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
	
	public boolean isInsertIgnore(String attrName){
		ColumnIgnoreStatus ignore = attrIgnores.get(attrName);
		if(ignore==null){
			return false;
		}
		return ignore.insertIgnore;
	}
	
	public boolean isUpdateIgnore(String attrName){
		ColumnIgnoreStatus ignore = attrIgnores.get(attrName);
		if(ignore==null){
			return false;
		}
		return ignore.updateIgnore;
	}
	
	public String getVersionProperty(){
		return this.versionProperty;
	}
	
	public String getVersionCol(){
		return this.versionCol;
	}
	
	static class ColumnIgnoreStatus{
		public boolean insertIgnore;
		public boolean updateIgnore;
		public ColumnIgnoreStatus(ColumnIgnore ignore){
			insertIgnore = ignore.insert();
			updateIgnore = ignore.update();
		}
		
		public ColumnIgnoreStatus(InsertIgnore ig,UpdateIgnore ug){
			insertIgnore = ig!=null;
			updateIgnore = ug!=null;
		}
		
	}

	public Class getTargetClass() {
		return targetClass;
	}
	public void setTargetClass(Class targetClass) {
		this.targetClass = targetClass;
	}
	
	
}
