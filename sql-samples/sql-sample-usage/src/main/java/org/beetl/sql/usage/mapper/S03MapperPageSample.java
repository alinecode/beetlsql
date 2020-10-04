package org.beetl.sql.usage.mapper;


import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.DefaultPageResult;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.usage.mapper.dao.UserPageMapper;

import java.util.HashMap;
import java.util.Map;

import static org.beetl.sql.sample.SampleHelper.printPageResult;

/**
 * 翻页演示
 */
public class S03MapperPageSample {
    UserPageMapper mapper = null;
    public S03MapperPageSample(UserPageMapper mapper){
        this.mapper = mapper;
    }
    public static void  main(String[] args){
        SQLManager sqlManager = SampleHelper.getSqlManager();
        UserPageMapper mapper = sqlManager.getMapper(UserPageMapper.class);
        S03MapperPageSample sample = new S03MapperPageSample(mapper);
        sample.callSqlManager();
        sample.sqlIdPage();
        sample.sqlIdPag2();


    }

    /**
     * mapper 没有内置方法提供翻页查询，可以调用sqlManager,可以参考S3PageSample 得到更多例子
     */
    public void  callSqlManager(){
        String sql = "select * from sys_user where name=?";
        SQLReady sqlReady = new SQLReady(sql,"ab");
        PageRequest request  = DefaultPageRequest.of(1,10);
        PageResult result = mapper.getSQLManager().execute(sqlReady, UserEntity.class,request);
        printPageResult((DefaultPageResult)result);

        //提供一个翻页模板sql，这里需要用page函数以生成查询总数sql和查询数据sql
        String sqlTemplate = "select #{page('*')} from sys_user where name=#{name}";
        Map map = new HashMap();
        map.put("name","ab");
        request  = DefaultPageRequest.of(1,10);
        result =  mapper.getSQLManager().executePageQuery(sqlTemplate,UserEntity.class,map,request);
        printPageResult((DefaultPageResult)result);

    }

    public void sqlIdPage(){
        PageRequest request  = DefaultPageRequest.of(1,10);
        PageResult result = mapper.pageQueryByCondition("ab",request);
        printPageResult((DefaultPageResult)result);
    }


    public void sqlIdPag2(){
        PageRequest request  = DefaultPageRequest.of(1,10);
        PageResult result = mapper.pageQueryByCondition2("ab",request);
        printPageResult((DefaultPageResult)result);
    }



}
