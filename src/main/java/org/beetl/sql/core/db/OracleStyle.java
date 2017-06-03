package org.beetl.sql.core.db;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class OracleStyle extends AbstractDBStyle {

    public OracleStyle() {
    }

    @Override
    public String getPageSQL(String sql) {
        //beetlT，beetl_rn 避免与sql重复
        String pageSql = "SELECT * FROM "
                + " ( "
                + " SELECT beeltT.*, ROWNUM beetl_rn "
                + " FROM ( \n" + sql + this.getOrderBy() + "\n )  beeltT "
                + " WHERE ROWNUM <" + HOLDER_START + DBStyle.PAGE_END + HOLDER_END
                + ") "
                + "WHERE beetl_rn >= " + HOLDER_START + DBStyle.OFFSET + HOLDER_END;
        return pageSql;
    }

    @Override
    public String getPageSQLStatement(String sql, long offset, long pageSize) {
        offset = PageParamKit.oracleOffset(this.offsetStartZero, offset);
        long pageEnd = PageParamKit.oraclePageEnd(offset, pageSize);

        int capacity = sql.length() + 133;
        StringBuilder builder = new StringBuilder(capacity);
        builder.append("SELECT * FROM ");
        builder.append(" ( ");
        builder.append(" SELECT beeltT.*, ROWNUM beetl_rn ");
        builder.append(" FROM ( \n").append(sql).append("\n )  beeltT ");
        builder.append(" WHERE ROWNUM < ").append(pageEnd);
        builder.append(" ) ");
        builder.append("WHERE beetl_rn >= ").append(offset);

        return builder.toString();
    }

    @Override
    public void initPagePara(Map<String, Object> paras, long start, long size) {
        long s = start + (this.offsetStartZero ? 1 : 0);
        paras.put(DBStyle.OFFSET, s);
        paras.put(DBStyle.PAGE_END, s + size);
    }

    @Override
    public int getIdType(Class c,String idProperty) {
    	 	List<Annotation> ans = BeanKit.getAllAnnoation(c, idProperty);
        int idType = DBStyle.ID_ASSIGN; // 默认是自增长

        for (Annotation an : ans) {
            if (an instanceof SeqID) {
                idType = DBStyle.ID_SEQ;
                //seq 总是优先
                break;
            } else if (an instanceof AssignID) {
                idType = DBStyle.ID_ASSIGN;
            }
        }

        return idType;

    }

    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    public int getDBType() {
        return DB_ORACLE;
    }


}
