package org.beetl.sql.core;

import org.beetl.sql.core.engine.SQLParameter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 批处理更新中的参数
 * @author xiandafu
 */
public class BatchUpdateInterceptorContext  extends  InterceptorContext{
    protected List<List<SQLParameter>> batchParas;
    public BatchUpdateInterceptorContext(String sqlId, String sql, List<List<SQLParameter>> batchParas ) {
        super(sqlId, sql, Collections.emptyList(), Collections.emptyMap(), true);
        this.batchParas = batchParas;
    }

    public List<List<SQLParameter>> getBatchParas() {
        return batchParas;
    }

    public void setBatchParas(List<List<SQLParameter>> batchParas) {
        this.batchParas = batchParas;
    }
}
