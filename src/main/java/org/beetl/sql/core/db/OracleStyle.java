package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.SeqID;

public class OracleStyle extends AbstractDBStyle {

	public OracleStyle() {
	}
	
	@Override
	public String getPageSQL(String sql) {
		//beetlT，beetl_rn 避免与sql重复
		String pageSql = "SELECT * FROM "
		+" ( "
		+" SELECT beeltT.*, ROWNUM beetl_rn "
		+" FROM ( \n" +sql+ this.getOrderBy()+"\n )  beeltT " 
		+" WHERE ROWNUM <"+HOLDER_START+DBStyle.PAGE_END+HOLDER_END
		+") "
		+"WHERE beetl_rn >= " +HOLDER_START+DBStyle.OFFSET+HOLDER_END ;
		return pageSql;
	}

	@Override
	public void initPagePara(Map<String, Object> paras,long start,long size) {
		long s = start+(this.offsetStartZero?1:0);
		paras.put(DBStyle.OFFSET,s);
		paras.put(DBStyle.PAGE_END,s+size);
	}

	@Override
	public int getIdType(Method idMethod) {
		Annotation[] ans = idMethod.getAnnotations();
		int idType = DBStyle.ID_ASSIGN; // 默认是自增长

		for (Annotation an : ans) {
			if (an instanceof SeqID) {
				idType = DBStyle.ID_SEQ;
				//seq 总是优先
				break ;
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
	
	@Override
	public String getEscapeForKeyWord(){
		return "";
	}
	
	

}
