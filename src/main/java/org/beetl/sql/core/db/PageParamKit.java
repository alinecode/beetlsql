package org.beetl.sql.core.db;


/**
 * <BR>
 * create time : 2017-05-29 22:12
 *
 * @author luoyizhu@gmail.com
 */
class PageParamKit {

    static long mysqlOffset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    static long postgresOffset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    static long oracleOffset(boolean offsetStartZero, long start) {
        return start + (offsetStartZero ? 1 : 0);
    }

    static long oraclePageEnd(long offset, long pageSize) {
        return offset + pageSize;
    }

    static long db2sqlOffset(boolean offsetStartZero, long start) {
        return start + (offsetStartZero ? 1 : 0);
    }

    static long db2sqlPageEnd(long offset, long pageSize) {
        return offset + pageSize - 1;
    }

    static long sqlLiteOffset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    static long sqlServerOffset(boolean offsetStartZero, long start) {
        return start + (offsetStartZero ? 1 : 0);
    }

    static long sqlServer2012Offset(boolean offsetStartZero, long start) {
        return start - (offsetStartZero ? 0 : 1);
    }

    static long sqlServerPageEnd(long offset, long pageSize) {
        return offset + pageSize - 1;
    }

}
