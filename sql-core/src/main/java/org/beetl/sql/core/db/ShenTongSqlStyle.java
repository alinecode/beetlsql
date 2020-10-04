package org.beetl.sql.core.db;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.core.range.RangeSql;
import org.beetl.sql.core.range.RowNumRange;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/6/14. 国产神通数据库
 * @author 66036623@qq.com
 */
public class ShenTongSqlStyle extends AbstractDBStyle {

	RowNumRange rowNumRangeHelper = null;
	public ShenTongSqlStyle() {
		rowNumRangeHelper = new RowNumRange(this);
	}



	@Override
	public int getIdType(Class c, String idProperty) {
		List<Annotation> ans = BeanKit.getAllAnnotation(c, idProperty);
		int idType = DBType.ID_ASSIGN; // 默认是自定义ID
		for (Annotation an : ans) {
			if (an instanceof SeqID) {
				idType = DBType.ID_SEQ;
				//seq 总是优先
				break;
			} else if (an instanceof AssignID) {
				idType = DBType.ID_ASSIGN;
			}
		}
		return idType;
	}

	@Override
	public String getName() {
		return "ShenTong";
	}

	@Override
	public int getDBType() {
		return DBType.DB_SHENGTONG;
	}

	@Override
	public RangeSql getRangeSql() {
		return this.rowNumRangeHelper;
	}

	@Override
	public String getSeqValue(String seqName) {
		return seqName + ".nextval";
	}

	private long pageOffset(boolean offsetStartZero, long start) {
		return start + (offsetStartZero ? 1 : 0);
	}

	private long pageEnd(long offset, long pageSize) {
		return offset + pageSize;
	}
}