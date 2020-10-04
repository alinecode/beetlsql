package org.beetl.sql.core;

import java.sql.Connection;
import java.util.Map;

/**
 * 多个数据源的代理，根据{@link Policy}决定使用哪个数据救援
 * @author xiandafu
 *
 */
public class ConditionalConnectionSource implements ConnectionSource {

    Policy policy;
    Map<String,ConnectionSource> all;
    ConnectionSource defaultCs;

    /**
     *
     * @param policy 选择数据源的策略
     * @param all 所有备选的数据源表
     */
    public ConditionalConnectionSource( Policy policy,Map<String,ConnectionSource> all){
        this.all = all;
        String defaultName = policy.getMasterName();
        defaultCs = all.get(defaultName);
        if(defaultCs==null){
            throw new IllegalArgumentException("根据 "+defaultName+" 找不到对应的ConnectionSource");
        }
        this.policy = policy;
    }
    @Override
    public Connection getMasterConn() {
        return defaultCs.getMasterConn();
    }



    @Override
    public Connection getMetaData() {
        return getMasterConn();
    }

    @Override
    public Connection getConn(ExecuteContext ctx, boolean isUpdate) {
        String name = policy.getConnectionSourceName(ctx,isUpdate);
        ConnectionSource cs = all.get(name);
        if(cs==null){
            throw new IllegalArgumentException("根据 "+name+" 找不到对应的ConnectionSource");
        }
        return cs.getConn(ctx,isUpdate);

    }

    /**
     * 子类需要覆盖决定如何使用事务
     * @return
     */
    @Override
    public boolean isTransaction() {
        return DSTransactionManager.inTrans();
    }
    public static interface Policy{
        String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate);
        String getMasterName();
    }
}
