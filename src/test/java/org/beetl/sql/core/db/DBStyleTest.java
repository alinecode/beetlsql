package org.beetl.sql.core.db;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <BR>
 * create time : 2017-05-29 13:30
 *
 * @author luoyizhu@gmail.com
 */
public class DBStyleTest {
    AbstractDBStyle style;
    boolean offsetStartZero;
    int pageNumber = 1;
    int pageSize = 20;
    long offset = (offsetStartZero ? 0 : 1) + (pageNumber - 1) * pageSize;

    String sql = "select * from tb_bee";

    Logger log = LoggerFactory.getLogger(DBStyleTest.class);

    int sqlLen = sql.length();

    @Before
    public void start() {
        log.info("sql.len: " + sqlLen);
    }


    @Test
    public void mySqlPageSqlStatement() throws Exception {
        info(new MySqlStyle());
    }

    private void info(AbstractDBStyle style) {

        String pageSql = style.getPageSQLStatement(sql, offset, pageSize);
        String type = style.getName();
        log.info("~~~~~~~~~~~~~~~~~~~~" + type + "~~~~~~~~~~~~~~~~~~~~");
        log.info(type + ".pageSql : " + pageSql);
        log.info(type + ".pageSql.lenth : " + pageSql.length());
        log.info(" 需要扩充:" + (pageSql.length() - sqlLen));
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println();
    }


    @Test
    public void postgresPageSqlStatement() throws Exception {
        info(new PostgresStyle());
    }

    @Test
    public void sqlServerPageSqlStatement() {
        info(new SqlServerStyle());
    }

    @Test
    public void oraclePageSqlStatement() {
        info(new OracleStyle());
    }

    @Test
    public void sqlLitePageSqlStatement() {
        info(new SQLiteStyle());
    }

    @Test
    public void h2PageSqlStatement() {
        info(new H2Style());
    }

    @Test
    public void db2SqlPageSqlStatement() {
        info(new DB2SqlStyle());
    }


}
