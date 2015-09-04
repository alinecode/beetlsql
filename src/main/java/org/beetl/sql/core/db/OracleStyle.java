package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.SeqID;

public class OracleStyle extends AbstractDBStyle {

	public OracleStyle() {
	}

	@Override
	public String getPageSQL(String sql) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initPagePara(Map paras,long start,long size) {
//		// TODO Auto-generated method stub
		paras.put(DBStyle.OFFSET,start);
		paras.put(DBStyle.PAGE_SIZE,size);
	}

	@Override
	public int getIdType(Method idMethod) {
		Annotation[] ans = idMethod.getAnnotations();
		int idType = DBStyle.ID_ASSIGN; // 默认是自增长

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
		return "oracle";
	}

}
