package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

/**
 * 数据库差异：h2.
 * 
 * @author zhoupan
 *
 */
public class H2Style extends AbstractDBStyle {

	public String getPageSQL(String sql) {
		return sql + this.getOrderBy()+ " \nlimit " + HOLDER_START + OFFSET + HOLDER_END + " , " + HOLDER_START + PAGE_SIZE + HOLDER_END;
	}

	public void initPagePara(Map<String, Object> param, long start, long size) {
		param.put(DBStyle.OFFSET, start);
		param.put(DBStyle.PAGE_SIZE, size);
	}

	public H2Style() {
		super();
	}

	public int getIdType(Method idMethod) {
		Annotation[] ans = idMethod.getAnnotations();
		int idType = DBStyle.ID_AUTO; // 默认是自增长

		for (Annotation an : ans) {
			if (an instanceof AutoID) {
				idType = DBStyle.ID_AUTO;
				continue;
			} else if (an instanceof SeqID) {
				// my sql not support
			} else if (an instanceof AssignID) {
				idType = DBStyle.ID_ASSIGN;
			}
		}

		return idType;

	}

	public String getName() {
		return "h2";
	}

	public String getEscapeForKeyWord() {
		return "\"";
	}
}
