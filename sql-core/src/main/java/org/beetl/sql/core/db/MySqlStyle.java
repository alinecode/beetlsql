package org.beetl.sql.core.db;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 数据库差异：mysql数据库
 *
 * @author xiandafu
 */
public class MySqlStyle extends AbstractDBStyle {

    RangeSql rangeSql = null;

    public MySqlStyle() {

        rangeSql = new OffsetLimitRange(this);
		this.keyWordHandler = new KeyWordHandler() {
			@Override
			public String getTable(String tableName) {
				return "`" + tableName + "`";

			}
			@Override
			public String getCol(String colName) {
				return "`" + colName + "`";
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
        return "mysql";
    }

    @Override
    public int getDBType() {
        return DBType.DB_MYSQL;
    }

    @Override
    public RangeSql getRangeSql() {
        return this.rangeSql;
    }



}
