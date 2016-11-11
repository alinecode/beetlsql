package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

/**
 * 数据库差异：mysql数据库
 * @author xiandafu
 *
 */
public class MySqlStyle extends AbstractDBStyle {
	
	
	
	@Override
	public String getPageSQL(String sql) {
		return sql+this.getOrderBy()+" \nlimit " + HOLDER_START + OFFSET + HOLDER_END + " , " + HOLDER_START + PAGE_SIZE + HOLDER_END;
	}

	@Override
	public void initPagePara(Map<String, Object> param,long start,long size) {
		param.put(DBStyle.OFFSET,start-(this.offsetStartZero?0:1));
		param.put(DBStyle.PAGE_SIZE,size);
	}

	public MySqlStyle() {
		this.keyWordHandler = new KeyWordHandler(){
			@Override
			public String getTable(String tableName) {
				return "`"+tableName+"`";
				
			}

			@Override
			public String getCol(String colName) {
				return "`"+colName+"`";
			}
			
		};
	}

	@Override
	public int getIdType(Method idMethod) {
		Annotation[] ans = idMethod.getAnnotations();
		int  idType = DBStyle.ID_AUTO ; //默认是自增长
		
		for(Annotation an :ans){
			if(an instanceof AutoID){
				idType = DBStyle.ID_AUTO;
				break;// 优先
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
		return "mysql";
	}

}
