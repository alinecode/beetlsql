package org.beetl.sql.test;

import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.annotatoin.Param;

/**
 * sqlProvider testcase
 * @author darren
 *
 */
public class UserSqlProvider {

    public String selectAll1(Integer id){
        StringBuilder sql = new StringBuilder("SELECT * FROM `user` WHERE 2 = 2 ");
        if (id!= null){
            sql.append("AND id = #id#");
        }
        return sql.toString();
    }

    public SQLReady selectAll2(Integer id){
        StringBuilder sql = new StringBuilder("SELECT * FROM `user` WHERE 2 = 2 ");
        if (id!= null){
            sql.append("AND id = ?");
        }
        SQLReady  sqlReady = new SQLReady(sql.toString(),new Object[]{id});
        return sqlReady;
    }

    public SQLReady delete2(Integer id){
        StringBuilder sql = new StringBuilder("delete from `user` WHERE 2 = 2 ");
        if (id!= null){
            sql.append("AND id = ?");
        }
        SQLReady  sqlReady = new SQLReady(sql.toString(),new Object[]{id});
        return sqlReady;
    }
}
