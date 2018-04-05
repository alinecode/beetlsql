package org.beetl.sql.core.mapper.builder;

import java.util.HashMap;
import java.util.Map;

import org.beetl.sql.core.mapper.InsertMapperInvoke;
import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.PageQueryMapperInvoke;
import org.beetl.sql.core.mapper.SQLReadyExecuteMapperInvoke;
import org.beetl.sql.core.mapper.SelecSingleMapperInvoke;
import org.beetl.sql.core.mapper.SelectMapperInvoke;
import org.beetl.sql.core.mapper.UpdateBatchMapperInvoke;
import org.beetl.sql.core.mapper.UpdateMapperInvoke;
import org.beetl.sql.core.mapper.internal.*;

/**
 * <pre>
 * MapperInvoke相关 静态配置数据
 * 为了不使用户阅读代码的时候造成混乱和方便重复使用数据.
 * 独立出来的一个静态数据配置
 * </pre>
 * create time: 2017/5/4 19:18
 *
 * @author luoyizhu
 */
public final class MapperInvokeDataConfig {
    /** 处理用户自定义方法的代理 */
    static final Map<Integer, MapperInvoke> METHOD_DESC_PROXY_MAP = new HashMap<Integer, MapperInvoke>();
    /**
     * beetlsql内置映射好的方法, 是提供给: AmiInnerProxyMapperInvoke 对象使用的.
     * 或者提供给其他自定义的BaseMapper使用
     */
    static final Map<String, MapperInvoke> INTERNAL_AMI_METHOD = new HashMap<String, MapperInvoke>();

    /** beetlsql BaseMapper的处理构建器 */
    static final MapperConfigBuilder BASE_MAPPER_BUILDER;

    static {
        // 处理用户自定义方法的代理, 提供给MethodDesc.type使用的服务.
        METHOD_DESC_PROXY_MAP.put(0, new InsertMapperInvoke());
        METHOD_DESC_PROXY_MAP.put(1, new InsertMapperInvoke());
        METHOD_DESC_PROXY_MAP.put(2, new SelecSingleMapperInvoke());
        METHOD_DESC_PROXY_MAP.put(3, new SelectMapperInvoke());
        METHOD_DESC_PROXY_MAP.put(4, new UpdateMapperInvoke());
        METHOD_DESC_PROXY_MAP.put(5, new UpdateBatchMapperInvoke());
        METHOD_DESC_PROXY_MAP.put(6, new PageQueryMapperInvoke());
    }

    static {
        // 添加内置的 INTERNAL_AMI_METHOD
        INTERNAL_AMI_METHOD.put("insert", new InsertAmi());
        INTERNAL_AMI_METHOD.put("insertReturnKey", new InsertReturnKeyAmi());
        INTERNAL_AMI_METHOD.put("updateById", new UpdateByIdAmi());
        INTERNAL_AMI_METHOD.put("updateTemplateById", new UpdateTemplateByIdAmi());
        INTERNAL_AMI_METHOD.put("deleteById", new DeleteByIdAmi());
        INTERNAL_AMI_METHOD.put("unique", new UniqueAmi());
        INTERNAL_AMI_METHOD.put("single", new SingleAmi());
        INTERNAL_AMI_METHOD.put("lock", new LockAmi());
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
        INTERNAL_AMI_METHOD.put("templatePage", new TemplatePageAmi());
        INTERNAL_AMI_METHOD.put("createQuery", new QueryAmi());
        INTERNAL_AMI_METHOD.put("createLambdaQuery", new LambdaQueryAmi());

        
        BASE_MAPPER_BUILDER = new MapperConfigBuilder();
    }
    //处理 @Sql注解
    public static MapperInvoke sqlReadyInvoke =  new SQLReadyExecuteMapperInvoke();

    /**
     * 如果用户要扩展, 推荐methodDescType变量使用1000以后的
     *
     * @param methodDescType 类型
     * @param mapperInvoke   映射类处理.
     */
    public static void putMethodDescProxy(int methodDescType, MapperInvoke mapperInvoke) {
        METHOD_DESC_PROXY_MAP.put(methodDescType, mapperInvoke);
    }

    public static MapperInvoke getMethodDescProxy(int methodDescType) {
        return METHOD_DESC_PROXY_MAP.get(methodDescType);
    }
    
    public static MapperInvoke getSQLReadyProxy(){
    		return sqlReadyInvoke;
    }


}
