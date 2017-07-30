package org.beetl.sql.core.kit;


import java.util.Map;

/**
 * 分页辅助工具 <BR>
 * create time : 2017-05-28 19:09
 *
 * @author luoyizhu@gmail.com
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

        // 是否存在 order by
        boolean hasOrderBy = sql.indexOf("    order by") != -1;
        boolean fromIndexOver = false;
        int fromIndex = 0;
        int fromEnd = 0;

        for (String s : sql.split("\n")) {
            if (!fromIndexOver&&s.equals("    from")) {
                fromIndexOver = true;
                if (hasOrderBy == false) {
                    break;
                }
            }

            if (s.equals("    order by")) {
                break;
            }

            if (!fromIndexOver) {
                fromIndex += s.length()+1;
            } 
            fromEnd += s.length()+1;

        }

        // 存在order by 就移除
        if (hasOrderBy) {
            return "select count(1) \n" + selectSql.substring(fromIndex, fromEnd);

        }

        return "select count(1) \n" + selectSql.substring(fromIndex);
    }
    
    public static void main(String[] args){
    		String sql = "select * from user #abcd# where 1=1 and c=#abc# order #text('acd.123/2')#";
    		sql = PageKit.getCountSql(sql);
    		System.out.println(sql);
    }

}
