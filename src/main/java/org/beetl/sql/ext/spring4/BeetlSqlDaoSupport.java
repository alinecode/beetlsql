package org.beetl.sql.ext.spring4;

import org.beetl.sql.core.SQLManager;
import org.springframework.dao.support.DaoSupport;

import static org.springframework.util.Assert.notNull;

/**
 * BeetlSql对Spring DAO 的支持
 * @author woate
 */
public class BeetlSqlDaoSupport extends DaoSupport {
    SQLManager sqlManager;

    @Override
    protected void checkDaoConfig() throws IllegalArgumentException {
        notNull(this.sqlManager, " 'sqlManager' 属性是必须的");
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public void setSqlManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }
}
