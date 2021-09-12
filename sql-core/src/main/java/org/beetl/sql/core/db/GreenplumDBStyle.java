/**
 * Project Name:myutils
 * Date:2021年9月8日
 * Copyright (c) 2021, jingma All Rights Reserved.
 */

package org.beetl.sql.core.db;

/**
 * Greenplum数据库 <br/>
 * date: 2021年9月8日 <br/>
 * @author jingma
 * @version
 */
public class GreenplumDBStyle extends PostgresStyle{
    @Override
    public String getName() {
        return "greenplum";
    }

    @Override
    public int getDBType() {
        return DBType.DB_GREENPLUM;
    }

}
