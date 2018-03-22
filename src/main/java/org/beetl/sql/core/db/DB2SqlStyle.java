package org.beetl.sql.core.db;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 数据库差异：mysql数据库
 *
 * @author xiandafu
 */
public class DB2SqlStyle extends AbstractDBStyle {


    public DB2SqlStyle() {
        this.keyWordHandler = new KeyWordHandler() {
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
    public String getPageSQL(String sql) {
        //db2 9 不支持limit
//		return sql+this.getOrderBy()+" \nlimit " + HOLDER_START + OFFSET + HOLDER_END + " , " + HOLDER_START + PAGE_SIZE + HOLDER_END;

        return " SELECT * FROM "
                + "("
                + "	SELECT inner_query_b.*, ROWNUMBER() OVER() beetl_rn  FROM   "
                + "	(   "
                + sql + this.getOrderBy()
                + "	) AS inner_query_b  "
                + " )AS inner_query_a WHERE inner_query_a.beetl_rn BETWEEN " + HOLDER_START + OFFSET + HOLDER_END + " and " + HOLDER_START + PAGE_END + HOLDER_END;


    }

    @Override
    public String getPageSQLStatement(String sql, long offset, long pageSize) {

        offset = PageParamKit.db2sqlOffset(this.offsetStartZero, offset);
        long pageEnd = PageParamKit.db2sqlPageEnd(offset, pageSize);

        int capacity = sql.length() + 180;
        StringBuilder builder = new StringBuilder(capacity);
        builder.append(" SELECT * FROM ").append("(");
        builder.append("	SELECT inner_query_b.*, ROWNUMBER() OVER() beetl_rn  FROM   ");
        builder.append("	(   ").append(sql).append("	) AS inner_query_b  ");
        builder.append(" )AS inner_query_a WHERE inner_query_a.beetl_rn BETWEEN ");
        builder.append(offset).append(" and ").append(pageEnd);

        return builder.toString();

    }

    @Override
    public void initPagePara(Map<String, Object> paras, long start, long size) {
        long s = start + (this.offsetStartZero ? 1 : 0);
        paras.put(DBStyle.OFFSET, s);

        paras.put(DBStyle.PAGE_END, s + size - 1);
    }

    @Override
    public int getIdType(Class c, String idProperty) {
        List<Annotation> ans = BeanKit.getAllAnnoation(c, idProperty);
        int idType = DBStyle.ID_AUTO; //默认是自增长

        for (Annotation an : ans) {
            if (an instanceof AutoID) {
                idType = DBStyle.ID_AUTO;
                break;// 优先
            } else if (an instanceof SeqID) {
            	 	idType = DBStyle.ID_SEQ;
            	 	break;
            } else if (an instanceof AssignID) {
                idType = DBStyle.ID_ASSIGN;
                break;
            }
        }

        return idType;

    }

    @Override
    public String getName() {
        return "db2";
    }

    @Override
    public int getDBType() {
        return DB_DB2;
    }
    //IBM驱动对插入null 严格遵守了jdbc规范，需要指定类型，参考BeanProcessor.setPreparedStatementPara
    @Override
    protected String appendSetColumnAbsolute(Class<?> c, TableDesc table, String colName, String fieldName) {
    	int type = table.getColDesc(colName).sqlType;
        return this.getKeyWordHandler().getCol(colName)  + "=" + HOLDER_START + fieldName+",jdbc='"+type+"'" + HOLDER_END + ",";
    }
    @Override
    protected String appendInsertValue(Class<?> c, TableDesc table, String fieldName,String col) {
    	int type = table.getColDesc(col).sqlType;
        return HOLDER_START + fieldName +",jdbc='"+ type+"'"+HOLDER_END + ",";

    }
    
    @Override
    public String getSeqValue(String seqName) {
		return "NEXT VALUE FOR "+ seqName+" ";
	}


}
