package org.beetl.sql.clazz;

import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.clazz.kit.CaseInsensitiveOrderSet;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 数据库表或者视图信息
 * @author xiandafu
 * @see ClassDesc
 */
public class TableDesc {

	/**
	 * 数据表
	 */
	private String name;
	/**
	 * 主键
	 */
	private Set<String> idNames= new CaseInsensitiveOrderSet<String>();

	/**
	 * 数据表注释,注意，并不是所有JDBC都默认能取得注释，可能需要一些数据库厂商特定的配置
	 */

	private String remark = null;
	/**
	 * 列名
	 */
	private Set<String> cols = new CaseInsensitiveOrderSet<String>();
	

	//跟table相关的类
	private Map<Class,ClassDesc> classes = new LinkedHashMap<Class,ClassDesc>();

	/**
	 * table 列的详细描述
	 */
	private CaseInsensitiveHashMap<String,ColDesc> colsDetail = new CaseInsensitiveHashMap<String,ColDesc>();
	//table所在的schema
	private String schema ;
	//tables所在的catalog
	private String catalog;

	//如果不为空，则标识这个表不存在，来源于realTableName
	private String realTableName;
	
	public TableDesc(String name, String remark){
		this.name = name;
		this.remark = remark;
	}
	
	public boolean containCol(String col){
		return cols .contains(col);
	}

	
	public void addCols(ColDesc col){
		colsDetail.put(col.getColName(), col);
		cols.add(col.getColName());
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
	 * 获得一个类的详细描述，用于代码生成
	 * @param c
	 * @param nc
	 * @return
	 */
	public ClassDesc genClassDesc(Class c, NameConversion nc){
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
	public MockClassDesc genClassDesc(NameConversion nc){
		MockClassDesc c = new MockClassDesc(this,nc);
		return c ;
	}

	public CaseInsensitiveHashMap<String, ColDesc> getColsDetail() {
		return colsDetail;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getRealTableName() {
		return realTableName;
	}

	public void setRealTableName(String realTableName) {
		this.realTableName = realTableName;
	}
}