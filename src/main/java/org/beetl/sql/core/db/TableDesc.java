package org.beetl.sql.core.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.NameConversion;

public class TableDesc{
	//保持大写
	private String name;
	// 默认为id，列明采用小写
	private String idName="id";
	// 采用大写
	private Set<String> cols = new HashSet<String>();
	
	private Set<String> metaCols = new HashSet<String>();
	private String metaName ;
	private String metaIdName;
	private Map<Class,ClassDesc> classes = new HashMap<Class,ClassDesc>();
	
	
	public TableDesc(String name){
		this.name = name.toUpperCase();
		this.metaName = name;
	}
	
	public boolean containCol(String col){
		return cols .contains(col.toUpperCase());
	}
	
	public void addCols(String col){
		cols.add(col.toUpperCase());
		metaCols.add(col);
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
	
	

	
	
}