package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.util.List;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

/**
 * openPlant麦杰实时数据库
 * 
 * @author linziguan
 *
 */
public class OpenPlantStyle extends AbstractDBStyle{
	protected RangeSql rangeSql = null;
	
	public OpenPlantStyle() {
		rangeSql = new OffsetLimitRange(this);
		this.keyWordHandler = new KeyWordHandler() {
			@Override
			public String getTable(String tableName) {
				return StringKit.addEscape(tableName,'`');

			}
			@Override
			public String getCol(String colName) {
				return StringKit.addEscape(colName,'`');

			}

		};
	}
	
	@Override
	public int getIdType(Class c,String idProperty) {
		List<Annotation> ans = BeanKit.getAllAnnotation(c, idProperty);
		int idType = DBType.ID_AUTO; //默认是自增长

		for (Annotation an : ans) {
			if (an instanceof AutoID) {
				idType = DBType.ID_AUTO;
				break;// 优先
			} else if (an instanceof SeqID) {
				//my sql not support
			} else if (an instanceof AssignID) {
				idType = DBType.ID_ASSIGN;
			}
		}

		return idType;

	}
	
	@Override
	public String getName() {
		return "openPlant";
	}

	@Override
	public int getDBType() {
		return DBType.DB_OPENPLANT;
	}

	@Override
	public RangeSql getRangeSql() {
		return this.rangeSql;
	}

}
