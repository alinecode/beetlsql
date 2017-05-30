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
    int pageSize = 2;
    long offset = (offsetStartZero ? 0 : 1) + (pageNumber - 1) * pageSize;

    String sql = "select * from tb_bee";

    Logger log = LoggerFactory.getLogger(DBStyleTest.class);

    @Before
    public void start() {
        log.info("sb");
    }


    @Test
    public void mySqlPageSQLStatement() throws Exception {
        style = new MySqlStyle();

        String pageSql = style.getPageSQLStatement(sql, offset, pageSize);

        log.info(pageSql);
    }


    @Test
    public void postgresPageSQLStatement() throws Exception {
        style = new PostgresStyle();

        String pageSql = style.getPageSQLStatement(sql, offset, pageSize);

        log.info(pageSql);
    }



}
