package org.beetl.sql.core.db;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.kit.CaseInsensitiveHashMap;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
/**
 * 数据库注解
 * @author xiandafu
 *
 */
public class TableDesc{
	
	private String name;
	private Set<String> idNames= new CaseInsensitiveOrderSet<String>();
	
	// 数据表注释
	private String remark = null;
	
	private Set<String> cols = new CaseInsensitiveOrderSet<String>();
	

	//跟table相关的类
	private Map<Class,ClassDesc> classes = new LinkedHashMap<Class,ClassDesc>();
	//table 列的详细描述
	private CaseInsensitiveHashMap<String,ColDesc> colsDetail = new CaseInsensitiveHashMap<String,ColDesc>();
	//table所在的schema
	private String schema ;
	
	public TableDesc(String name,String remark){
		this.name = name;
		this.remark = remark;
	}
	
	public boolean containCol(String col){
		return cols .contains(col);
	}
	
	public String getExactCol(String col){
		for(String str:cols){
			if(str.equalsIgnoreCase(col)){
				return str;
			}
		}
		//不可能发生
		throw new RuntimeException("call containCol first to ensure exist");
	}
	
	public void addCols(ColDesc col){
		colsDetail.put(col.colName, col);
		cols.add(col.colName);
	}
	
	public ColDesc getColDesc(String name){
		return (ColDesc)colsDetail.get(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public Set<String> getIdNames() {
		return idNames;
	}

	public void addIdName(String idName) {
		this.idNames.add(idName);
		
	}

	public Set<String> getCols() {
		return cols;
	}



	public String getRemark() {
		return remark;
	}

	/**
	 * 获得一个类的详细描述
	 * @param c
	 * @param nc
	 * @return
	 */
	public ClassDesc getClassDesc(Class c,NameConversion nc){
		ClassDesc classDesc = classes.get(c);
		if(classDesc==null){
			synchronized(classes){
				classDesc = classes.get(c);
				if(classDesc!=null) return classDesc;
				classDesc = new ClassDesc(c,this,nc);
				classes.put(c, classDesc);
				
			}
		}
		
		return classDesc;
	}
	
	/** 根据table得到一个对应的class描述，仅仅用于代码生成
	 * @param nc
	 * @return
	 */
	public ClassDesc getClassDesc(NameConversion nc){
		ClassDesc c = new ClassDesc(this,nc);
		return c ;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	
	
	
	
	

	
	
}