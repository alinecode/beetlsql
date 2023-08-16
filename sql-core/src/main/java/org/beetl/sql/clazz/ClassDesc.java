package org.beetl.sql.clazz;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.clazz.kit.CaseInsensitiveOrderSet;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 找到bean定义和数据库定义共有的部分，作为实际操作的sql语句
 * @author xiandafu
 *
 */
public class ClassDesc {
	/**
	 * 实体类
	 */
	protected Class targetClass;
	/**
	 * 关联的数据库表信息
	 */
	protected TableDesc table;
	/**
	 * 命名转换
	 */
	protected NameConversion nc;
	/**
	 * 实体类的属性名
	 */
	protected Set<String> properties = new CaseInsensitiveOrderSet<String>();
	/* 记录table和pojo的交集 */
	protected Set<String> cols = new CaseInsensitiveOrderSet<String>();

	/**
	 * 主健属性和主键字段
	 * */
	protected List<String> idProperties = new ArrayList<String>(3);
	protected List<String> idCols = new ArrayList<String>(3);
	/**
	 * id的getter/setter方法
	 * */
	protected Map<String, Object> idMethods = new CaseInsensitiveHashMap<String, Object>();

	/**
	 * 当前实体类的所有注解信息
	 * */
	protected ClassAnnotation ca = null;

	public ClassDesc(Class c, TableDesc table, NameConversion nc) {
		this.targetClass = c;
		this.nc = nc;
		ca = ClassAnnotation.getClassAnnotation(c);
		PropertyDescriptor[] ps = ca.getPropertyDescriptor(c);

		Set<String> ids = table.getIdNames();

		Table tableAnnotation = (Table)c.getAnnotation(Table.class);
		if(tableAnnotation!=null){
			if(tableAnnotation.assignID()){
				//使用pojo定义的assignId，忽略数据库定义
				ids =findIdColByAnnotation(c,ps);
				if(ids.isEmpty()){
					throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND,"使用@Table(assignId=true) in "+c+" 但是未发现@AssingId");
				}
				table.getIdNames().clear();
				ids.forEach( id->table.addIdName(id));
			}
		}



		CaseInsensitiveHashMap<String, PropertyDescriptor> tempMap = new CaseInsensitiveHashMap<String, PropertyDescriptor>();

		for (PropertyDescriptor p : ps) {
			//所有属性必须有getter和setter
			if (p.getReadMethod() != null && p.getWriteMethod() != null) {
				String property = p.getName();
				String col = nc.getColName(c, property);
				if (col != null) {
					tempMap.put(col, p);
				}
			}
		}

		//取交集
		for (String col : table.getCols()) {
			if (tempMap.containsKey(col)) {
				PropertyDescriptor p = (PropertyDescriptor) tempMap.get(col);
				String attrName = p.getName();
				addAttribute(col,attrName);
				if (ids.contains(col)) {
					addIdAttribute(col,attrName);
					Method readMethod = p.getReadMethod();
					idMethods.put(attrName, readMethod);

				}

			}
		}


	}

	private void addIdAttribute(String col,String attr){
		//保持同一个顺序
		idProperties.add(attr);
		idCols.add(col);
	}
	private void addAttribute(String col,String attr){
		//dsfsdf
		properties.add(attr);
		cols.add(col);
	}

	/**
	 * 用于代码生成，一个虚拟的ClassDesc，
	 * @param table
	 * @param nc
	 */
	protected ClassDesc(TableDesc table, NameConversion nc) {
		this.table = table;
		this.nc = nc;
		for (String colName : table.getCols()) {
			String prop = nc.getPropertyName(colName);
			this.properties.add(prop);
			ColDesc colDes = table.getColDesc(colName);
			this.cols.add(colName);
		}
		for (String name : table.getIdNames()) {
			this.idProperties.add(nc.getPropertyName(name));
		}


	}

	public ClassDesc buildVirtualClass(TableDesc table, NameConversion nc) {
		return new ClassDesc(table, nc);
	}


	public List<String> getIdAttrs() {
		return this.idProperties;
	}

	public String getIdAttr() {
		if (this.idProperties.size() > 1) {
			throw new UnsupportedOperationException("不支持多主键");
		}
		return idProperties.get(0);
	}

	public List<String> getIdCols() {
		return idCols;
	}

	public Set<String> getAttrs() {
		return properties;
	}


	public Set<String> getInCols() {
		return this.cols;
	}

	public Map<String, Object> getIdMethods() {
		return this.idMethods;
	}

	/**
	 * 修正拼写错误
	 * @return ClassAnnotation
	 */
	public ClassAnnotation getClassAnnotation() {
		return ca;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public Set<String> findIdColByAnnotation(Class cls,PropertyDescriptor[] ps){
		Set<String> idCols = new CaseInsensitiveOrderSet <>();
		for(PropertyDescriptor pd:ps){
			String name = pd.getName();
			List<Annotation>  list = BeanKit.getAllAnnotation(cls,name);
			for(Annotation annotation:list){
				if(annotation instanceof AssignID){
					idCols.add(nc.getColName(cls,name));
				}
			}
		}
		return idCols;
	}
}
