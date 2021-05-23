package org.beetl.sql.test.annotation;

import org.beetl.sql.annotation.builder.TargetAdditional;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据业务逻辑从{@code tenantSchemaLocals} 取出schema,设置到动态参数里
 */
public class SchemaTenantContext implements TargetAdditional {
    public static ThreadLocal<String> tenantSchemaLocals = new ThreadLocal<>();
    @Override
    public Map<String, Object> getAdditional(ExecuteContext ctx, Annotation an) {

        String schema = tenantSchemaLocals.get();
        if(schema==null){
            throw new IllegalStateException("缺少租户信息");
        }
        Map map = new HashMap();
        map.put("schema",schema);
        return map;
    }
}
