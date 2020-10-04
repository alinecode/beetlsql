package org.beetl.sql.core.db;


/**
 * <BR>
 * create time : 2017-05-29 22:12
 * TODO,改成更通过用的以0或者以1作为翻页其实记录的俩个函数，而不是每个数据库都来一个
 * @author luoyizhu@gmail.com
 */
public class PageParamKit {

    public static long mysqlOffset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    public static long postgresOffset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    public static long oracleOffset(boolean offsetStartZero, long start) {
        return start + (offsetStartZero ? 1 : 0);
    }

    public static long oraclePageEnd(long offset, long pageSize) {
        return offset + pageSize;
    }

    public static long db2sqlOffset(boolean offsetStartZero, long start) {
        return start + (offsetStartZero ? 1 : 0);
    }

    public static long db2sqlPageEnd(long offset, long pageSize) {
        return offset + pageSize - 1;
    }

    public static long sqlLiteOffset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    public static long sqlServerOffset(boolean offsetStartZero, long start) {
        return start + (offsetStartZero ? 1 : 0);
    }

    public static long sqlServer2012Offset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    public static long sqlServerPageEnd(long offset, long pageSize) {
        return offset + pageSize - 1;
    }

}
