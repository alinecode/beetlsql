package org.beetl.sql.core.range;

import java.util.Map;

/**
 * 数据库范围查询，输入sql或者模板语句，得出范围查询sql
 * @author xiandafu
 */
public interface RangeSql {
    /**
     * 翻页辅助变量
     */

     final String PAGE_FLAG="beetl_rn";
    /**
     * 转化jdbcsql成范围查询语句
     * @param jdbcSql
     * @param objOffset 起始位置，对于mysql等传统数据库有语法支持，但对hive等可能没有此参数，但offset可以作为其他参数做相对位置
     * @param limit
     * @return
     */

    String toRange(String jdbcSql, Object objOffset,Long limit);

    String toTemplateRange(Class mapping,String template);

    void addTemplateRangeParas(Map<String, Object> paras, Object objOffset, long size);
}
