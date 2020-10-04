package org.beetl.sql.test;

import org.beetl.sql.core.*;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 如何自定义Interceptor,定义了一个SqlIdAppendInterceptor，总是在sql语句最前面增加一个注释，
 * 注释内容为sqlId标识符，方便数据库dba与程序员沟通
 *
 *
 *
 * @author xiandafu
 */
public class InterceptSample {

    public InterceptSample() {

    }

    public static void main(String[] args) throws Exception {
        InterceptSample interceptSample = new InterceptSample();
        interceptSample.appendSqlId();
        interceptSample.debug();
    }

    /**
     *在给执行的jdbc sql 增加一个注释
     */
    public void appendSqlId(){
        SQLManager sqlManager = SampleHelper.init();
        //新增加一个Interceptor
        List< Interceptor> interceptors = new ArrayList(Arrays.asList(sqlManager.getInters()));
        SqlIdAppendInterceptor sqlIdAppendInterceptor = new SqlIdAppendInterceptor();
        //放到第一个，这样后面的DebugInteceptor能打印出sql，实际项目应该放到最后
        interceptors.add(0,sqlIdAppendInterceptor);
        sqlManager.setInters(interceptors.toArray(new Interceptor[0]));


        sqlManager.unique(UserEntity.class,1);
        sqlManager.execute(new SQLReady("select * from sys_user"), UserEntity.class);


    }

    /**
     * 自带的DebugInterceptor输出详细信息到控制台，这里切换到用日志框架
     */
    public void debug(){
        SQLManager sqlManager = SampleHelper.init();
        LogDebugInterceptor logDebugInterceptor = new LogDebugInterceptor();
        sqlManager.setInters(new Interceptor[]{logDebugInterceptor});
        sqlManager.unique(UserEntity.class,1);

    }




    public static class SqlIdAppendInterceptor implements  Interceptor{

        @Override
        public void before(InterceptorContext ctx) {

            ExecuteContext context = ctx.getExecuteContext();
            if(context.sqlId.isSql()){
            	return ;
			}
            String jdbcSql = context.sqlResult.jdbcSql;
            String info  = context.sqlId.toString();
            //为发送到数据库的sql增加一个注释说明，方便数据库dba能与开发人员沟通
            jdbcSql = "/*"+info+"*/\n"+jdbcSql;
            context.sqlResult.jdbcSql = jdbcSql;
        }

        @Override
        public void after(InterceptorContext ctx) {
            //dothing
        }
        @Override
        public void exception(InterceptorContext ctx, Exception ex) {
            //do nothing
        }
    }


    public static class LogDebugInterceptor extends DebugInterceptor {
        Logger logger = Logger.getLogger("simple");
        @Override
        protected void println(String str) {
            logger.log(Level.INFO,str);
        }
        @Override
        protected void error(String str) {
            logger.log(Level.SEVERE,str);
        }
    }

}
