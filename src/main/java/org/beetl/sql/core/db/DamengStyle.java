package org.beetl.sql.core.db;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 数据库差异：达梦数据库
 *  
 * @author Stylesmile 1403539106@qq.com
 */
public class DamengStyle extends AbstractDBStyle {
    public DamengStyle() {
        this.keyWordHandler = new KeyWordHandler() {
            @Override
            public String getTable(String tableName) {
                return  tableName ;
            }
            @Override
            public String getCol(String colName) {
                return colName ;
            }
        };
    }

    @Override
    public String getPageSQL(String sql) {
        return sql + this.getOrderBy() + " \nlimit " + HOLDER_START + OFFSET + HOLDER_END + " , " + HOLDER_START + PAGE_SIZE + HOLDER_END;
    }

    @Override
    public String getPageSQLStatement(String sql, long offset, long pageSize) {
        offset = PageParamKit.mysqlOffset(this.offsetStartZero, offset);
        StringBuilder builder = new StringBuilder(sql);
        builder.append(" limit ").append(offset).append(" , ").append(pageSize);
        return builder.toString();
    }

    @Override
    public void initPagePara(Map<String, Object> param, long start, long size) {
        param.put(DBStyle.OFFSET, start - (this.offsetStartZero ? 0 : 1));
        param.put(DBStyle.PAGE_SIZE, size);
    }

    @Override
    public int getIdType(Class c,String idProperty) {
                List<Annotation> ans = BeanKit.getAllAnnoation(c, idProperty);
        int idType = DBStyle.ID_AUTO; //默认是自增长
        for (Annotation an : ans) {
            if (an instanceof AutoID) {
                idType = DBStyle.ID_AUTO;
                break;// 优先
            } else if (an instanceof SeqID) {
                idType = DBStyle.ID_SEQ;
            } else if (an instanceof AssignID) {
                idType = DBStyle.ID_ASSIGN;
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
        return DB_DAMENG;
    }

}