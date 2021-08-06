package org.beetl.sql.gen;

import lombok.Data;
import org.beetl.sql.clazz.TableDesc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 表对应的实体
 */
@Data
public class Entity {
	/**
	 * 类名
	 */
	String name;
	/**
	 * 表名
	 */
	String tableName;
	/**
	 * 数据库catalog
	 */
	String catalog;
	/**
	 * 表的备注，<b>注意</b> 有些数据库不支持，或者需要jdbc url配置才能获取表注释
	 */
	String comment;
	/**
	 * 所有属性
	 */
	ArrayList<Attribute> list = new ArrayList<Attribute>();

	/**
	 * {@link Entity#list}  对应属性的java全限定名，比如，日期类型，则这里会有java.util.Date
	 * java.lang下的所有类名不在这里，java能自动导入，不需代码生成里提供
	 */

	Set<String> importPackage = new TreeSet<>();

	/**
	 * 跟此实体相关的表描述
	 */
	TableDesc tableDesc;


	public List<String>  getCols(){
		return list.stream().map(attribute -> attribute.getColName()).collect(Collectors.toList());
	}



}
