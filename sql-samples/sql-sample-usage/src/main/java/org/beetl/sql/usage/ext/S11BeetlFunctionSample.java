package org.beetl.sql.usage.ext;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;

import java.util.*;

/**
 * beetlsql 使用了Beetl脚本语言，这里演示如何做一些常用扩展
 */
public class S11BeetlFunctionSample {
    SQLManager sqlManager = null;
    public S11BeetlFunctionSample(SQLManager sqlManager){
        this.sqlManager = sqlManager;
    }
    public static void  main(String[] args) {
        SQLManager sqlManager = SampleHelper.getSqlManager();
        S11BeetlFunctionSample sample = new S11BeetlFunctionSample(sqlManager);
        sample.isEmpty();
        sample.addNewFunction();
    }

    /**
     * 默认的isEmpty函数 ，对于空字符串，返回false，可以使用新的isEmpty实现来支持空字符串，空集合
     */
    public void isEmpty(){

        {
            String sql = "select count(1) from sys_user where 1=#{ isEmpty(str)?0:1 }";
            Map map = new HashMap();
            map.put("str","");
            long count = sqlManager.execute(sql,Integer.class,map).get(0);
            System.out.println("before "+count);
        }

        GroupTemplate groupTemplate = groupTemplate();
        /**
         * 也可以通过配置beetlsql的配置文件btsql-ext.properties
         * fn.isEmpty=org.beetl.ext.fn.EmptyExpressionFunction
         * fn.isNotEmpty=org.beetl.ext.fn.IsNotEmptyExpressionFunction
         *
         */
        groupTemplate.registerFunction("isEmpty",new org.beetl.ext.fn.EmptyExpressionFunction());
        groupTemplate.registerFunction("isNotEmpty",new org.beetl.ext.fn.IsNotEmptyExpressionFunction());

        {
            String sql = "select count(1) from sys_user where 1=#{ isEmpty(str)?0:1 }";
            Map map = new HashMap();
            map.put("str","");
            long count = sqlManager.execute(sql,Integer.class,map).get(0);
            //应该为0
            System.out.println("after "+count);
        }

    }

    /**
     * 新增一个beetl函数nextDay，返回入参的下一天
     */
    public void addNewFunction(){
        GroupTemplate groupTemplate = groupTemplate();
        groupTemplate.registerFunction("nextDay",new NextDayFunction());
        Map map = new HashMap();
        map.put("date",new Date());
        String sql = "select * from sys_user where create_time is not null and create_time<#{nextDay(date)}";
        List<UserEntity> count = sqlManager.execute(sql,UserEntity.class,map);
    }





    protected  GroupTemplate groupTemplate(){
        BeetlTemplateEngine beetlTemplateEngine =  (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
        GroupTemplate groupTemplate = beetlTemplateEngine.getBeetl().getGroupTemplate();
        return groupTemplate;
    }

    /**
     * beetl 内置函数，返回下一天
     */
    public static class NextDayFunction implements Function {

        @Override
        public Object call(Object[] paras, Context ctx) {
            Date date = (Date) paras[0];
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_YEAR, 1); // 今天+1天
            return c.getTime();
        }
    }


}
