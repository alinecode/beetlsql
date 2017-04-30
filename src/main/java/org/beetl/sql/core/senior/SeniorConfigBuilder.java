package org.beetl.sql.core.senior;

import org.beetl.sql.core.mapper.*;
import org.beetl.sql.core.mapper.internal.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 动静结合的一个高级配置构建器.
 *
 * 高级的灵活配置构建器, 满足喜欢捣腾的用户
 * 理论上这个类该写在 {@link SeniorConfig} 文件里.
 * 但为了清晰理解代码, 独立了出来.
 * </pre>
 * create time : 2017-04-27 15:28
 *
 * @author luoyizhu@gmail.com
 */
public final class SeniorConfigBuilder {
    /**
     * 处理用户自定义方法的代理
     */
    static final MapperInvoke[] METHOD_DESC_PROXY_ARRAY;
    /**
     * mapper接口与代理类关联
     */
    static final Map<Class, MapperInvoke> MAPPER_JOIN_PROXY_MAPPER_INVOKE = new HashMap<Class, MapperInvoke>();
    /**
     * 用户扩展的方法.
     * 映射好的方法, 是提供给: AmiInnerProxyMapperInvoke 对象使用的.
     */
    static final Map<String, MapperInvoke> INTERNAL_AMI_METHOD = new HashMap<String, MapperInvoke>();

    /** 用户扩展基接口 */
    static final AmiInnerProxyMapperInvoke AMI_PROXY = new AmiInnerProxyMapperInvoke();

    // 内置配置
    static {
        // 添加内置的 INTERNAL_AMI_METHOD
        INTERNAL_AMI_METHOD.put("insert", new InsertAmi());
        INTERNAL_AMI_METHOD.put("insertReturnKey", new InsertReturnKeyAmi());
        INTERNAL_AMI_METHOD.put("updateById", new UpdateByIdAmi());
        INTERNAL_AMI_METHOD.put("updateTemplateById", new UpdateTemplateByIdAmi());
        INTERNAL_AMI_METHOD.put("deleteById", new DeleteByIdAmi());
        INTERNAL_AMI_METHOD.put("unique", new UniqueAmi());
        INTERNAL_AMI_METHOD.put("single", new SingleAmi());
        INTERNAL_AMI_METHOD.put("all", new AllAmi());
        INTERNAL_AMI_METHOD.put("allCount", new AllCountAmi());
        INTERNAL_AMI_METHOD.put("template", new TemplateAmi());
        INTERNAL_AMI_METHOD.put("templateOne", new TemplateOneAmi());
        INTERNAL_AMI_METHOD.put("templateCount", new TemplateCountAmi());
        INTERNAL_AMI_METHOD.put("updateByIdBatch", new UpdateByIdBatchAmi());
        INTERNAL_AMI_METHOD.put("execute", new ExecuteAmi());
        INTERNAL_AMI_METHOD.put("executeUpdate", new ExecuteUpdateAmi());
        INTERNAL_AMI_METHOD.put("insertBatch", new InsertBatchAmi());
        INTERNAL_AMI_METHOD.put("getSQLManager", new GetSQLManagerAmi());
        INTERNAL_AMI_METHOD.put("insertTemplate", new InsertTemplateAmi());

        // beetlsql内置的基接口, 使用 InnerMapperInvoke 处理.(为了不改变原有代码, 将来推荐统一使用AmiInnerProxyMapperInvoke).
        MAPPER_JOIN_PROXY_MAPPER_INVOKE.put(BaseMapper.class, new InnerMapperInvoke());


        // 处理用户自定义方法的代理, 提供给MethodDesc.type使用的服务.
        METHOD_DESC_PROXY_ARRAY = new MapperInvoke[7];
        METHOD_DESC_PROXY_ARRAY[0] = new InsertMapperInvoke();
        METHOD_DESC_PROXY_ARRAY[1] = new InsertMapperInvoke();
        METHOD_DESC_PROXY_ARRAY[2] = new SelecSingleMapperInvoke();
        METHOD_DESC_PROXY_ARRAY[3] = new SelectMapperInvoke();
        METHOD_DESC_PROXY_ARRAY[4] = new UpdateMapperInvoke();
        METHOD_DESC_PROXY_ARRAY[5] = new UpdateBatchMapperInvoke();
        METHOD_DESC_PROXY_ARRAY[6] = new PageQueryMapperInvoke();
    }

    private MethodDescBuilder methodDescBuilder;

    SeniorConfigBuilder() {
    }

    /**
     * 添加接口与代理的映射
     *
     * @param c                 一般填写接口
     * @param mapperInvokeProxy 如果是自定义基接口, 一般对应 {@link AmiInnerProxyMapperInvoke} 即可高度扩展. 也可以自定义一个proxy
     */
    public static void putMapperInvokeProxy(Class c, MapperInvoke mapperInvokeProxy) {
        MAPPER_JOIN_PROXY_MAPPER_INVOKE.put(c, mapperInvokeProxy);
    }

    /**
     * <pre>
     *
     * 添加基接口与代理的映射 {@link AmiInnerProxyMapperInvoke}
     * 可以添加多个
     * 如果想给你的基接口扩展额外的内置方法, 请使用 addAmi 方法
     *
     * </pre>
     *
     * @param c 基接口
     */
    public static void addBaseMapper(Class c) {
        MAPPER_JOIN_PROXY_MAPPER_INVOKE.put(c, AMI_PROXY);
    }

    /**
     * <pre>
     * 用户扩展MapperInvoke, 后添加的 methodName 会覆盖相同的 methodName
     * 这里添加的扩展是为了给  {@link AmiInnerProxyMapperInvoke} 服务的.
     * </pre>
     *
     * @param methodName      方法名
     * @param mapperInvokeAmi MapperInvoke
     */
    public static void addAmi(String methodName, MapperInvoke mapperInvokeAmi) {
        INTERNAL_AMI_METHOD.put(methodName, mapperInvokeAmi);
    }

    public void build() {
        SeniorConfig.$.methodDescBuilder = this.getMethodDescBuilder();
    }

    MethodDescBuilder getMethodDescBuilder() {
        if (this.methodDescBuilder == null) {
            this.methodDescBuilder = new MethodDescBuilder() {
                @Override
                public MethodDesc create() {
                    return new MethodDesc();
                }
            };
        }

        return this.methodDescBuilder;
    }

    public void setMethodDescBuilder(MethodDescBuilder methodDescBuilder) {
        this.methodDescBuilder = methodDescBuilder;
    }
}
