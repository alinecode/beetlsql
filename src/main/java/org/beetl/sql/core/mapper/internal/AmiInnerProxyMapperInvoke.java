package org.beetl.sql.core.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.senior.SeniorConfig;

import java.lang.reflect.Method;

/**
 * <pre>
 * 灵活的策略代理, 用户可自由添加任何接口方法实现类
 * 通过 {@link org.beetl.sql.core.senior.SeniorConfigBuilder}
 * </pre>
 * create time : 2017-04-27 16:26
 *
 * @author luoyizhu@gmail.com
 */
public class AmiInnerProxyMapperInvoke implements MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, String namespace, Method m, Object[] args) {
        String methodName = m.getName();

        MapperInvoke invoke = SeniorConfig.$.getInternalAmi(methodName);

        if (invoke != null) {
            return invoke.call(sm, entityClass, namespace, m, args);
        }

        throw new UnsupportedOperationException(m.getName());
    }
}
