package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

public class SqlServerStyle extends AbstractDBStyle {

	public SqlServerStyle() {
	}


	@Override
	public String getPageSQL(String sql) {
		return "with query as ( select inner_query.*, row_number() over (order by current_timestamp) as beetl_rn from ( "
				+ sql.replaceFirst("(?i)select", "select top("+HOLDER_START+PAGE_END+HOLDER_END+") ")+ this.getOrderBy()
				+" ) inner_query ) select * from query where beetl_rn between "+HOLDER_START+OFFSET+HOLDER_END+" and "+HOLDER_START+PAGE_END+HOLDER_END;
	}

//	@Override
//	public void initPagePara(Map<String, Object> paras,long start,long size) {
//		paras.put(DBStyle.OFFSET,(start-1)*size+1);//开始索引   (pageNumber-1)*pageSize+1
//		paras.put(DBStyle.PAGE_END,start*size);//结束索引 pageNumber*pageSize
//	}

	
	@Override
	public void initPagePara(Map<String, Object> paras,long start,long size) {
		long s = start+(this.offsetStartZero?1:0);
		paras.put(DBStyle.OFFSET,s);
		paras.put(DBStyle.PAGE_END,s+size-1);
	}


	@Override
	public String getName() {
		return "sqlserver";
	}
	
	@Override
	public String getEscapeForKeyWord(){
		return "";
	}

}
