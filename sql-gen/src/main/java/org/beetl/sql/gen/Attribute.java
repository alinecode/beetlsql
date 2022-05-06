package org.beetl.sql.gen;

import lombok.Data;
import org.beetl.sql.clazz.kit.StringKit;

/**
 * 属性字段，根据数据库列生成的java属性,可以根据这些信息进行代码生成
 *
 * @author xiandafu
 * @see SourceConfig
 */
@Data
public class Attribute {
	/**
	 * 根据命名规则得出来的列对应的属性名
	 */
	private String name;
	private String colName;
	/**
	 * Class名，比如Integer
	 */
	private String javaType;
	private boolean isId;
	private boolean isAuto;
	/**
	 * 属性的注释
	 */
	private String comment;

	public String getMethodName() {
		return name.length() > 2 ? StringKit.toUpperCaseFirstOne(name) : name;
	}

}
