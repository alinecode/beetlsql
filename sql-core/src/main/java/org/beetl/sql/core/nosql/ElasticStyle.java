package org.beetl.sql.core.nosql;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * elastic search
 * 无白金版序列号，无法验证，放弃
 * https://www.elastic.co/guide/en/elasticsearch/reference/7.8/sql-jdbc.html
 *
 * @author xiandafu
 */
@Deprecated
public class ElasticStyle extends AbstractDBStyle {

    RangeSql rangeSql = null;
    public ElasticStyle() {
        super();
        rangeSql = new OffsetLimitRange(this);
        throw new UnsupportedOperationException("无白金版序列号，无法验证，放弃");
    }

    @Override
    public int getIdType(Class c,String idProperty) {
        List<Annotation> ans = BeanKit.getAllAnnotation(c, idProperty);
        int idType = DBType.ID_UNKNOWN;
        for (Annotation an : ans) {
            if (an instanceof AutoID) {
                //clickhouse sql not support
            } else if (an instanceof SeqID) {
                //clickhouse sql not support
            } else if (an instanceof AssignID) {
                idType = DBType.ID_ASSIGN;
                break;
            }
        }
        if(idType!=DBType.ID_ASSIGN){
            throw new IllegalArgumentException("ClickHouse只支持@AssignID,请在 "+idProperty+"上增加@AssignID注解");
        }

        return idType;
    }

    @Override
    public boolean  isNoSql(){
        return true;
    }
    @Override
    public String getName() {
        return "es";
    }

    @Override
    public int getDBType() {
        return DBType.DB_ES;
    }

    @Override
    public RangeSql getRangeSql() {
        return rangeSql;
    }

}
