package org.beetl.sql.test;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.sample.SampleHelper;
import org.beetl.sql.sample.entity.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DbStyle 是Beetlsql的核心之一，这里演示如何扩展底层JDBC的一些参数`,BeetlSQL很少提供对外配置JDBC细节
 *
 */
public class DbStyleSample {

    public DbStyleSample() {

    }

    public static void main(String[] args) throws Exception {
        DbStyleSample dbStyleSample = new DbStyleSample();
        dbStyleSample.preparedStatement();
    }


    public void preparedStatement(){
        DataSource dataSource = SampleHelper.datasource();
        ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        SQLManagerBuilder builder = new SQLManagerBuilder(source);
        builder.setNc(new UnderlinedNameConversion());
        builder.setInters(new Interceptor[]{new DebugInterceptor()});
        //实现某些特殊功能
        builder.setDbStyle(new H2Style());
        SQLManager sqlManager = builder.build();


        sqlManager.all(UserEntity.class);

    }

    public static class H2StylePlus extends MySqlStyle {
        @Override
        public SQLExecutor buildExecutor(ExecuteContext executeContext){
            return  new MyExecutor(executeContext);
        }

        /**
         * 考虑jdbc fetch size
         */
        public static class MyExecutor extends  BaseSQLExecutor{

            public MyExecutor(ExecuteContext executeContext) {
                super(executeContext);
            }
            @Override
            protected ResultSetHolder dbQuery(Connection conn, String sql, List<SQLParameter> jdbcPara) throws SQLException {
                if(this.getExecuteContext().target!= UserEntity.class){
                    return super.dbQuery(conn,sql,jdbcPara);
                }
                //对于UserEntity对象查询，考虑使用特殊设置
                PreparedStatement ps = conn.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ps.setFetchSize(100000);
                this.setPreparedStatementPara(ps, jdbcPara);
                ResultSet rs = ps.executeQuery();
                return new ResultSetHolder(ps, rs);
            }

        }
    }

}
