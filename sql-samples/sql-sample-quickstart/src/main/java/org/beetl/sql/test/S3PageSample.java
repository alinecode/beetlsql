package org.beetl.sql.test;




import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.DefaultPageResult;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;
import org.beetl.sql.test.mapper.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 演示翻页,或者查询指定范围的数据,BeetlSQL默认配置都从1开始，自动翻译成目标数据库的起始位置
 * 也可以配置BeetlSQL从0开始
 *
 * BeetlSQL提供了翻页帮助函数，也可以为了优化，自己提供俩个sql，一个求总数，一个翻页查询
 * @author xiandafu
 *
 */

public class S3PageSample {

    SQLManager sqlManager;
    UserMapper mapper =null;

    public S3PageSample(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
        mapper = sqlManager.getMapper(UserMapper.class);
    }

    public static void main(String[] args) throws Exception {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S3PageSample page = new S3PageSample(sqlManager);
//        page.baseRange();
//        page.page();
//        page.jdbcPage();
//        page.resourceSqlPage();
//        page.resourceGroupSqlPage();
//        page.jdbcMapperPage();
        page.templateMapperPage();
    }

    /**
     * 范围查询
     */
    public void baseRange(){
        List<UserEntity> all = mapper.all();
        long count = mapper.allCount();

        UserEntity template = new UserEntity();
        template.setDepartmentId(1);
        UserEntity user1 = mapper.templateOne(template);




    }

    /**
     * 翻页查询，使用模板sql
     */

    public void page(){

        /**
         * sql模板语句的page函数能自动把sql模板语句转为为求总数语句
         */
        String sql = "select #{page('*')} from sys_user where department_id=#{id}";
        PageRequest request = DefaultPageRequest.of(1,10);
        Map map = new HashMap<>();
        map.put("id",1);
        PageResult pr = sqlManager.executePageQuery(sql,UserEntity.class,map,request);
        //强制转化为DefaultPageResult,
        DefaultPageResult pageResult = (DefaultPageResult)pr;
        printPageResult(pageResult);

    }

    /**
     * 直接使用jdbc sql
     */
    public void jdbcPage(){

        /**
         * 解析jdbc sql语句，生成求总数语句
         */
        String sql = "select * from sys_user where department_id=?";
        PageRequest request = DefaultPageRequest.of(1,10);
        SQLReady sqlReady = new SQLReady(sql,new Object[]{1});
        PageResult pr = sqlManager.execute(sqlReady,UserEntity.class,request);
        DefaultPageResult pageResult = (DefaultPageResult)pr;
        printPageResult(pageResult);

    }


    /**
     * 翻页查询通常很复杂，SQL很长，把sql语句放到sql文件里是个好办法，也是最常用的办法
     */
    public void resourceSqlPage(){
        PageRequest request = DefaultPageRequest.of(1,10);
        PageResult pr = mapper.pageQuery(1,request);
        DefaultPageResult pageResult = (DefaultPageResult)pr;
       printPageResult(pageResult);
    }

    /**
     * 对分组语句进行翻页查询，需要嵌套在子查询里，比如
     * <pre>
     *     select count(1),name from sys_user group by name
     * </pre>
     * 如上分组提供给beetlsql的时候，应该编写成
     * <pre>
     *    select #{page()} from ( select count(1),name from sys_user group by name ) a
     * </pre>
     *
     */
    public void resourceGroupSqlPage(){
        PageRequest request = DefaultPageRequest.of(1,10);

        PageResult pr = mapper.pageQuery2(1,request);
        DefaultPageResult pageResult = (DefaultPageResult)pr;
        printPageResult(pageResult);
    }


	public void jdbcMapperPage(){
		PageRequest request = DefaultPageRequest.of(1,10);

		PageResult pr = mapper.queryDeptById(1,request);
		DefaultPageResult pageResult = (DefaultPageResult)pr;
		printPageResult(pageResult);
	}

	public void templateMapperPage(){
		PageRequest request = DefaultPageRequest.of(1,10);
		PageResult pr = mapper.queryTemplateDeptById(1,request);
		DefaultPageResult pageResult = (DefaultPageResult)pr;
		printPageResult(pageResult);
	}


    public void printPageResult(DefaultPageResult pageResult){
        System.out.println(pageResult.getPage());
        System.out.println(pageResult.getPageSize());
        System.out.println(pageResult.getTotalRow());
        System.out.println(pageResult.getTotalPage());
        System.out.println(pageResult.getList());
    }

}
