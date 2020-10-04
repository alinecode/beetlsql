package org.beetl.sql.test;


import lombok.Data;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.annotation.builder.UpdateTimeConvert;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.*;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.DefaultPageResult;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.test.mapper.UserMapper;

import javax.naming.Name;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 演示like，batchUpdate,in 操作
 *
 * @author xiandafu
 */

public class S4Other {

    SQLManager sqlManager;
    UserMapper mapper = null;

    public S4Other(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
        mapper = sqlManager.getMapper(UserMapper.class);
    }

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S4Other others = new S4Other(sqlManager);
        others.like();
        others.in();
        others.batch();
        others.sqlResult();

    }

    /**
     * like
     */
    public void like() {
        String sql = "select * from sys_user where name like #{name}";
        Map paras = new HashMap();
        String name = "%li%";
        paras.put("name", name);
        List<UserEntity> users = sqlManager.execute(sql, UserEntity.class, paras);
        //同样效果
        sql = "select * from sys_user where name like #{'%'+name+'%'}";
        paras = new HashMap();
        name = "li";
        paras.put("name", name);
        users = sqlManager.execute(sql, UserEntity.class, paras);

        //同样小姑
        SQLReady sqlReady = new SQLReady("select * from sys_user where name like ?"
                ,new Object[]{"%"+name+"%"});
        users = sqlManager.execute(sqlReady,UserEntity.class);


    }

    /**
     * in
     */
    public void in() {
        //使用beetlsql提供的join函数,接受一个list变量
        String sql = "select * from sys_user where id in ( #{join(ids)} )";
        List list = Arrays.asList(1,2,3,4,5);
        Map paras = new HashMap();
        paras.put("ids", list);
        List<UserEntity> users = sqlManager.execute(sql, UserEntity.class, paras);

    }

    /**
     * batch
     */
    public void batch() {
        //批量插入
        UserEntity user1 = new UserEntity();
        user1.setName("b1");
        user1.setDepartmentId(1);

        UserEntity user2 = new UserEntity();
        user2.setName("b2");
        user2.setDepartmentId(1);


        //根据组件批量更新
        List<UserEntity> data = Arrays.asList(user1,user2);
        sqlManager.insertBatch(UserEntity.class,data);

        data.get(1).setName("bb11");
        sqlManager.updateByIdBatch(data);

        //循环删除，执行多次
        data.stream().forEach(userEntity -> mapper.deleteById(userEntity.getId()));

    }

    /**
     * 不执行，只得到sql语句和参数
     */
    public void sqlResult(){

        Map map = new HashMap();
        map.put("name","li");
        SQLResult sqlResult = sqlManager.getSQLResult(SqlId.of("user","select"),map);
        String targetJdbc = sqlResult.jdbcSql;
        Object[] paras = sqlResult.toObjectArray();
        System.out.println(targetJdbc);
        System.out.println(Arrays.asList(paras));

    }


}
