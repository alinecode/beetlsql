package org.beetl.sql.core.senior;

import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.mapper.InnerMapperInvoke;
import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.MethodDesc;
import org.beetl.sql.core.mapper.internal.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
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
     * mapper接口与代理类关联
     */
    static final Map<Class, MapperInvoke> MAPPER_JOIN_PROXY_MAPPER_INVOKE = new HashMap<Class, MapperInvoke>();
    static final Map<String, MapperInvoke> INTERNAL_AMI_METHOD = new HashMap<String, MapperInvoke>();

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

        MAPPER_JOIN_PROXY_MAPPER_INVOKE.put(BaseMapper.class, new InnerMapperInvoke());

    }

    private MethodDescBuilder methodDescBuilder;
    private Class baseMapperClass;

    SeniorConfigBuilder() {
    }

    public void build() {
        SeniorConfig.$.methodDescBuilder = this.getMethodDescBuilder();
    }


    public void putMapperInvokeProxy(Class c, MapperInvoke mapperInvokeProxy) {
        MAPPER_JOIN_PROXY_MAPPER_INVOKE.put(c, mapperInvokeProxy);
    }

    /**
     * <pre>
     * 用户扩展MapperInvoke, 后添加的 methodName 会覆盖相同的 methodName
     * </pre>
     *
     * @param methodName      方法名
     * @param mapperInvokeAmi MapperInvoke
     */
    public void putInternalAmi(String methodName, MapperInvoke mapperInvokeAmi) {
        INTERNAL_AMI_METHOD.put(methodName, mapperInvokeAmi);
    }

    private MethodDescBuilder getMethodDescBuilder() {
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

    private Class getBaseMapperClass() {
        if (this.baseMapperClass == null) {
            this.baseMapperClass = BaseMapper.class;
        }
        return baseMapperClass;
    }

    public void setBaseMapperClass(Class baseMapperClass) {
        this.baseMapperClass = baseMapperClass;
    }

}
