package org.beetl.sql.annotation.builder;

import org.beetl.sql.clazz.kit.AutoSQLEnum;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBStyle;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于注解扩展，任何注解的builder 提供了此类子类，那么属性在映射pojo和result时候，使用此机制
 * 如果实现 toAutoSqlPart，则代码生成的时候，此返回值作为代码参数的一部分。
 *
 *
 * 参考{@ UpdateTime}
 * @param <ATTR>  Bean的属性类型
 * @param <DBCOL> 数据库属性类型
 *  @author xiandafu
 */

@Plugin
public interface AttributeConvert {

	/**
	 * 把属性对象转成数据库值,如json对象转为字符串。如果需要进一步控制如何转化，可以使用自定义注解
	 * <pre>{@
	 *     @ToJackson
	 *     @OtherJacksonCofig(nullEnable=true)
	 *     private Order order
	 * }</pre>
	 * 这里，ToJackson是自定义注解，使用{@code @Builder}标注，用于转化，如果需要额外配置，你不得不在自定义一个注解，比如OtherJacksonCofig
	 *  然后联合cls和name通过反射或者BeanKit提供的API获得你的OtherJacksonConfig
	 *  <pre>
	 *
	 *     OtherJacksonCofig config =  BeanKit.getAnnotation(cls,name,OtherJacksonCofig.class);
	 *  </pre>
	 * @param ctx
	 * @param cls
	 * @param name 属性名
	 * @param pojo  传入的Pojo
	 * @return 返回值必须是jdbc能识别的类型，如Java的原始类型
	 */
	default Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {
		return BeanKit.getBeanProperty(pojo, name);
	}

	/**
	 * 把数据库值映射成java对象值，比如json转成fastjson或者jackson实体
	 *
	 * @param ctx
	 * @param cls
	 * @param name
	 * @param rs
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	default Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {

		return rs.getObject(index);
	}

	/**
	 * 如果返回不为null的字符串，则认为在自动生成sql语句的片段时候，使用返回值是一个表达式模板作为属性引用。
	 * 其中属性使用符号$$代替。比如，
	 * 1 )在postgres自动生成的insert语句中，需要转成json格式，返回
	 * "$$::JSON",则生成sql片段的时候，根据类型，生成的是 #{attrName}::JSON, 或者“?::JSON”
	 *  $$将会被代替成适合的sql片段
	 * 2) 任意数据库，想使用额外的函数，cast($$),则生成sql片段时候，返回cast(#{attrName} 或者 cast(?)
	 *
	 *
	 *
	 * @param dbStyle
	 * @param autoSQLEnum
	 * @param name
	 * @return
	 */
	default  String toAutoSqlPart(DBStyle dbStyle,Class cls,AutoSQLEnum autoSQLEnum, String name){
		return null;
	}


}
