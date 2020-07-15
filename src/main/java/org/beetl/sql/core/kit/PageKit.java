package org.beetl.sql.core.kit;


import java.util.Map;

/**
 * 分页辅助工具 <BR>
 * create time : 2017-05-28 19:09
 *
 * @author luoyizhu@gmail.com
 * @author xiandafu
 */
public final class PageKit {
    static String pageNumberName = "pageNumber";
    static String pageSizeName = "pageSize";

    static int pageSizeValue = 20;

   /**
     * sql格式化工具
     *
     * @param sql 正常的sql语句
     * @return 格式化后的sql语句
     */
    public static String formatSql(String sql) {
        return SqlFormatter.format(sql);
    }

    /**
     * 获取当前页码
     *
     * @param paras 参数map
     * @return 如果没有找到, 默认返回1
     */
    public static int getPageNumber(Map<String, Object> paras) {
        Integer pageNumber = (Integer) paras.get(pageNumberName);

        return pageNumber == null ? 1 : pageNumber.intValue();
    }

    /**
     * 获取每页显示的数量
     *
     * @param paras 参数map
     * @return 如果没有找到, 返回设置的默认大小
     */
    public static int getPageSize(Map<String, Object> paras) {
        Integer pageSize = (Integer) paras.get(pageSizeName);

        return pageSize == null ? pageSizeValue : pageSize.intValue();
    }

    public static String getCountSql(String selectSql) {

        selectSql = PageKit.formatSql(selectSql);
        String sql = selectSql.toLowerCase();

        // 是否存在最外层 order by
        int orderByIndex = sql.indexOf("\n    order by");
        //最外层的from
        int fromIndex = sql.indexOf("\n    from") ;
        // 存在order by 就移除
        if (orderByIndex!=-1) {
            return "select count(1) \n" + selectSql.substring(fromIndex, orderByIndex);
        }
        //简单的改成count
        return "select count(1) \n" + selectSql.substring(fromIndex);
    }
    
    public static void main(String[] args){
    		String sql = "select * from xxx where a=1 group a  order by a desc";
    		sql = PageKit.getCountSql(sql);
    		System.out.println(sql);
    }

}
