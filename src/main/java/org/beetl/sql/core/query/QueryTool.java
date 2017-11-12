package org.beetl.sql.core.query;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.DBStyle;

/**
 * @ClassName: QueryTool
 * @Description: 查询工具类，未查询器提供帮助
 * @author GavinKing
 * @date 2017/11/5
 *
 */
public class QueryTool {

    private static DBStyle dbStyle;

    public static SQLManager sqlManager;
    /**
     * 初始化查询工具
     * @param sqlManager
     */
    public static void init(SQLManager sqlManager) {
        QueryTool.sqlManager = sqlManager;
        QueryTool.dbStyle = sqlManager.getDbStyle();
    }

    /****
     * 根据实体class获取表名
     * @param c
     * @return
     */
    public static String getTableName(Class<?> c){
        return dbStyle.getNameConversion().getTableName(c);
    }
}
