package org.beetl.sql.core.nosql;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.meta.MetadataManager;

import java.util.Collection;

/**
 * Doris 支持，虽然像Mysql，但细节不像，比如范围查询需要有order by,翻页正确写法是
 * <pre>
 * select
 * ===
 *
 * ```sql
 *
 * select #{page("*")} from example_tbl_agg1
 * -- :pageIgnore(){
 * order by user_id
 * -- :}
 * ```
 * </pre>
 *
 * 也不支持主键，但可以类似clickhouse那样，假定一个@AssignId主键
 */
public class DorisStyle extends ClickHouseStyle {
	@Override
	public String getName() {
		return "doris";
	}

	@Override
	public int getDBType() {
		return DBType.DB_DORIS;
	}


	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs){
		metadataManager = new DorisMetaDataManager(cs,this);
		return metadataManager;
	}

}
