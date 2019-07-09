package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;

import java.lang.annotation.Annotation;

/**
 * 返回一个Beetl函数，参考
 */
public class SampleUpdateTimeBuilder implements AttributePersistBuilder {

	@Override
	public String toSql(AbstractDBStyle dbStyle, String fieldName, String colName, Annotation an, TableDesc tableDesc) {
		//返回一个当前时间
		return "date()";
	}
}
