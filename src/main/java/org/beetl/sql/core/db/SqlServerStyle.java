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
		//假定总是用id做主键，否则，只能自己写分页语句
		String pageSql = "select top "+HOLDER_START+"text("+DBStyle.PAGE_END+")"+HOLDER_END +" * from  "
		+" ( "
		+" select row_number() over(order by id) as rownumber,beetlT.* from ( "
		+ sql+ this.getOrderBy()
		+") beetlT ) beetlT2  "
		+" where beetlT2.rownumber >="  +HOLDER_START+DBStyle.OFFSET+HOLDER_END ;
		return pageSql;
	}

	@Override
	public void initPagePara(Map<String, Object> paras,long start,long size) {
		long s = start+(this.offsetStartZero?1:0);
		paras.put(DBStyle.OFFSET,s);
		paras.put(DBStyle.PAGE_END,size);
	}

	@Override
	public int getIdType(Method idMethod) {
		Annotation[] ans = idMethod.getAnnotations();
		int  idType = DBStyle.ID_AUTO ; //默认是自增长
		
		for(Annotation an :ans){
			if(an instanceof AutoID){
				idType = DBStyle.ID_AUTO;
				continue ;
			}else if(an instanceof SeqID){
				//my sql not support 
			}else if(an instanceof AssignID){
				idType =DBStyle.ID_ASSIGN;
			}
		}
		
		return idType;

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
