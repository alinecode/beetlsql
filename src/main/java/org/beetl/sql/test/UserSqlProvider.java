package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.Param;

/**
 * sqlProvider testcase
 * @author darren
 * @date 2019/4/29 16:13
 */
public class UserSqlProvider {

    public String selectAll1(Integer id){
        StringBuilder sql = new StringBuilder("SELECT * FROM `user` WHERE 2 = 2 ");
        if (id!= null){
            sql.append("AND id = #id#");
        }
        return sql.toString();
    }
}
