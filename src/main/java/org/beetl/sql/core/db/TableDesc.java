package org.beetl.sql.core.db;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.NameConversion;

public class TableDesc{
	//保持大写
	private String name;
	// 默认为id，列明采用小写
	private String idName="id";
	
	private String remark = null;
	// 采用大写,为了方便查询
	private Set<String> cols = new LinkedHashSet<String>();
	
	private Set<String> metaCols = new LinkedHashSet<String>();
	private String metaName ;
	private String metaIdName;
	//跟table相关的类
	private Map<Class,ClassDesc> classes = new LinkedHashMap<Class,ClassDesc>();
	//table 列的详细描述
	private Map<String,ColDesc> colsDetail = new LinkedHashMap<String,ColDesc>();
	//table所在的schema
	private String schema ;
	
	public TableDesc(String name,String remark){
		this.name = name.toUpperCase();
		this.metaName = name;
		this.remark = remark;
	}
	
	public boolean containCol(String col){
		return cols .contains(col.toUpperCase());
	}
	
	public void addCols(ColDesc col){
		colsDetail.put(col.colName, col);
		
		cols.add(col.colName.toUpperCase());
		metaCols.add(col.colName);
	}
	
	public ColDesc getColDesc(String name){
		return colsDetail.get(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getMetaCols() {
		return metaCols;
	}

	public void setMetaCols(Set<String> metaCols) {
		this.metaCols = metaCols;
	}

	public String getMetaName() {
		return metaName;
	}

	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName.toUpperCase();
		this.metaIdName = idName;
	}

	public Set<String> getCols() {
		return cols;
	}

	public void setCols(Set<String> cols) {
		this.cols = cols;
	}

	public String getMetaIdName() {
		return metaIdName;
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