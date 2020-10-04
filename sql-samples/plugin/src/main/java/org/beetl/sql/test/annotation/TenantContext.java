package org.beetl.sql.test.annotation;

import org.beetl.sql.annotation.builder.TargetAdditional;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class TenantContext implements TargetAdditional {
    public static ThreadLocal<Integer> tenantLocals = new ThreadLocal<>();
    @Override
    public Map<String, Object> getAdditional(ExecuteContext ctx, Annotation an) {
        Map map = new HashMap();
        Integer tenant = tenantLocals.get();
        if(tenant==null){
            throw new IllegalStateException("缺少租户信息");
        }

        map.put("tenantId",tenant);
        map.put("updateTime",System.currentTimeMillis());

        if(tenant>99999){
            //可以根据租户信息，切换不同的数据源，这里设置一个标记。
            //ConnectionSouce#getConn方法能根据ExecuteContext决定使用哪个数据源。如何实现多数据源或者多表操作，可以参考S6MoreDatabase
            ctx.setContextPara("datasource","vip");
        }
        return map;
    }
}
