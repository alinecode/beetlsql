package org.beetl.sql.core;

import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.engine.SQLParameter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 对于某些jdbc只支持Statement，不支持PreparedStatement
 * @author xiandafu
 * @see DBStyle#preparedStatementSupport()
 * @see DBStyle#wrapStatementValue
 */
public class BaseStatementOnlySQLExecutor extends BaseSQLExecutor {
    public BaseStatementOnlySQLExecutor(ExecuteContext executeContext) {
        super(executeContext);
    }


    @Override
    public int[] insertBatch(Class<?> target,List<?> list){
        //不能确定是否支持，先抛出一个不支持
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] updateBatch(List<?> list){
        //不能确定是否支持，先抛出一个不支持
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] sqlReadyBatchExecuteUpdate(SQLBatchReady batch){
        //不能确定是否支持，先抛出一个不支持
        throw new UnsupportedOperationException();
    }


    @Override
    protected ResultSetHolder dbQuery(Connection conn, String sql, List<SQLParameter> jdbcPara) throws SQLException {
        //忽略jdbcPara，因为参数已经拼接在sql语句里了
        Statement statement = conn.createStatement();
        ResultSet rs  = statement.executeQuery(sql);
        return new ResultSetHolder(statement,rs);

    }
    @Override
    protected ResultUpdateHolder dbUpdate(Connection conn,String sql,List<SQLParameter> jdbcPara) throws SQLException{
        Statement statement = conn.createStatement();
        int  result  = statement.executeUpdate(sql);
        return new ResultUpdateHolder(statement,result);
    }
    @Override
    protected ResultUpdateHolder dbUpdateWithHolder(Connection conn,String sql,List<SQLParameter> jdbcPara,String[] cols) throws SQLException{
        Statement statement = conn.createStatement();
        int result = -1;
        if (cols!=null) {
            result  = statement.executeUpdate(sql,cols);

        }else{
            result  = statement.executeUpdate(sql);
        }
        return new ResultUpdateHolder(statement,result);
    }
}
