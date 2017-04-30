package org.beetl.sql.core.senior;

import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.MethodDesc;

import static org.beetl.sql.core.senior.SeniorConfigBuilder.*;

/**
 * <pre>
 * 高级的灵活配置, 实际使用中不需要理会这个配置类
 * 满足喜欢捣腾的用户
 * </pre>
 * create time : 2017-04-27 15:27
 *
 * @author luoyizhu@gmail.com
 */
public final class SeniorConfig {
    public static final SeniorConfig $ = new SeniorConfig();
    SeniorConfigBuilder seniorConfigBuilder = new SeniorConfigBuilder();
    MethodDescBuilder methodDescBuilder = seniorConfigBuilder.getMethodDescBuilder();

    private SeniorConfig() {
    }

    /**
     * @return 配置构建器
     */
    public SeniorConfigBuilder getBuilder() {
        return seniorConfigBuilder;
    }

    public MethodDesc createMethodDesc() {
        return methodDescBuilder.create();
    }

    public MapperInvoke getMapperInvokeProxy(Class c) {
        return MAPPER_JOIN_PROXY_MAPPER_INVOKE.get(c);
    }


    public MapperInvoke getInternalAmi(String methodName) {
        return INTERNAL_AMI_METHOD.get(methodName);
    }

    /**
     * 请使用 MethodDesc.type 来访问
     *
     * @param methodDescType 处理用户方法的下标
     * @return 处理用户方法的代理
     */
    public MapperInvoke getMethodDescProxy(int methodDescType) {
        return METHOD_DESC_PROXY_ARRAY[methodDescType];
    }

}
