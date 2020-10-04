package org.beetl.sql.core.mapper;

import org.beetl.sql.core.SQLReady;

public class UserSqlProvider {
    public SQLReady queryProviderById(Integer id){
        String sql =  "select * from sys_user where id = ?";
        SQLReady sqlReady = new SQLReady(sql,id);
        return sqlReady;
    }

    public String  queryTemplateProviderById(Integer id){
        String sql =  "select * from sys_user where id = #{id}";
        return sql;
    }


}

