package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.SeqID;

public class PostgresStyle extends AbstractDBStyle {

	public PostgresStyle() {
	}

	@Override
	public String getPageSQL(String sql) {
		String pageSql = "select _a.* from ( \n"
		+sql
		+" \n) _a "
		+" limit "+ HOLDER_START+ this.PAGE_SIZE+HOLDER_END+" offset "+ HOLDER_START+ this.OFFSET+HOLDER_END;
		return pageSql;
	}

	@Override
	public void initPagePara(Map<String, Object> paras,long start,long size) {
//		// TODO Auto-generated method stub
		paras.put(DBStyle.OFFSET,start-1);
		paras.put(DBStyle.PAGE_SIZE,size);
	}

	@Override
	public int getIdType(Method idMethod) {
		Annotation[] ans = idMethod.getAnnotations();
		int idType = DBStyle.ID_AUTO; // 默认是自增长

		for (Annotation an : ans) {
			if (an instanceof SeqID) {
				idType = DBStyle.ID_SEQ;
			} else if (an instanceof AssignID) {
				idType = DBStyle.ID_ASSIGN;
			}
		}

		return idType;

	}

	@Override
	public String getName() {
		return "postgres";
	}
	
	@Override
	public String getEscapeForKeyWord(){
		return "";
	}

}
