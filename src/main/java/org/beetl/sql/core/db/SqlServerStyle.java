package org.beetl.sql.core.db;

import java.util.Map;

public class SqlServerStyle extends AbstractDBStyle {

	public SqlServerStyle() {
		this.keyWordHandler = new KeyWordHandler(){
			@Override
			public String getTable(String tableName) {
				return "["+tableName+"]";
				
			}

			@Override
			public String getCol(String colName) {
				return "["+colName+"]";
			}
			
		};
	}


	@Override
	public String getPageSQL(String sql) {
		return "with query as ( select inner_query.*, row_number() over (order by current_timestamp) as beetl_rn from ( "
				+ sql.replaceFirst("(?i)select(\\s+distinct\\s+)?", "$0 top("+HOLDER_START+PAGE_END+HOLDER_END+") ")+ this.getOrderBy()
				+" ) inner_query ) select * from query where beetl_rn between "+HOLDER_START+OFFSET+HOLDER_END+" and "+HOLDER_START+PAGE_END+HOLDER_END;
	}


	

	
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
	public final int getDBType() {
		return DB_SQLSERVER;
	}
	

}
