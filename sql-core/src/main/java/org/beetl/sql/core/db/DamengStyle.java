package org.beetl.sql.core.db;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.DefaultKeyWordHandler;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 数据库差异：达梦数据库
 *  
 * @author Stylesmile 1403539106@qq.com
 */
public class DamengStyle extends AbstractDBStyle {
    RangeSql rangeSql = null;
    public DamengStyle() {
        this.keyWordHandler = new DefaultKeyWordHandler();

        rangeSql = new OffsetLimitRange(this);
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
                idType = DBType.ID_SEQ;
            } else if (an instanceof AssignID) {
                idType = DBType.ID_ASSIGN;
            }
        }
        return idType;
    }

    @Override
    public String getName() {
        return "dameng";
    }
    @Override
    public int getDBType() {
        return DBType.DB_DAMENG;
    }
    @Override
    public RangeSql getRangeSql() {
        return this.rangeSql;
    }

}