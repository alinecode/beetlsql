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
public class DB2SqlStyle extends AbstractDBStyle {
	
	
	
	@Override
	public String getPageSQL(String sql) {
		//db2 9 不支持limit
//		return sql+this.getOrderBy()+" \nlimit " + HOLDER_START + OFFSET + HOLDER_END + " , " + HOLDER_START + PAGE_SIZE + HOLDER_END;
	
		return  " SELECT * FROM "  
		+"("   
		+"	SELECT inner_query_b.*, ROWNUMBER() OVER() beetl_rn  FROM   "
		+"	(   "
		+sql+this.getOrderBy()
		+"	) AS inner_query_b  " 
		+" )AS inner_query_a WHERE inner_query_a.beetl_rn BETWEEN "+HOLDER_START+OFFSET+HOLDER_END+" and "+HOLDER_START+PAGE_END+HOLDER_END;   
		

	}

	@Override
	public void initPagePara(Map<String, Object> paras,long start,long size) {
		long s = start+(this.offsetStartZero?1:0);
		paras.put(DBStyle.OFFSET,s);
		paras.put(DBStyle.PAGE_END,s+size-1);
	}

	public DB2SqlStyle() {
		this.keyWordHandler = new KeyWordHandler(){
			@Override
			public String getTable(String tableName) {
				return tableName;
				
			}

			@Override
			public String getCol(String colName) {
				return colName;
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
		return "db2";
	}

	@Override
	public  int getDBType() {
		return DB_DB2;
	}

}
