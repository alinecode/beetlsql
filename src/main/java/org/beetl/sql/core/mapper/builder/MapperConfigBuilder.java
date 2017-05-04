package org.beetl.sql.core.mapper.builder;

import org.beetl.sql.core.mapper.*;
import org.beetl.sql.core.mapper.internal.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 动静结合的一个高级配置构建器.
 *
 * 高级的灵活配置构建器, 满足喜欢捣腾的用户
 * 理论上这个类该写在 {@link MapperConfig} 文件里.
 * 但为了清晰理解代码, 独立了出来.
 * </pre>
 * create time : 2017-04-27 15:28
 *
 * @author luoyizhu@gmail.com,xiandafu
 */
public final class MapperConfigBuilder {
   
    /**
     * mapper接口与代理类关联,key是接口，比如BaseMapper，value是个map，映射了方法到具体的处理类
     */
    final Map<Class, Map<String,MapperInvoke>> mapperProxyInvoke = new HashMap<Class, Map<String,MapperInvoke>>();
    /**
     * 系统默认提供的baseMapper所需要的method和处理类，参考BaseMapper
     */
     final Map<String, MapperInvoke> baseMapperInvoke = new HashMap<String, MapperInvoke>();
     /**
      * 处理用户自定义方法的代理
      */
     final MapperInvoke[] METHOD_DESC_PROXY_ARRAY;
   


    private MethodDescBuilder methodDescBuilder;

    private MapperConfig mapperConfig;
    
    public MapperConfigBuilder(MapperConfig mapperConfig) {
    		this.mapperConfig = mapperConfig;
    	 // 添加内置的 INTERNAL_AMI_METHOD
        baseMapperInvoke.put("insert", new InsertAmi());
        baseMapperInvoke.put("insertReturnKey", new InsertReturnKeyAmi());
        baseMapperInvoke.put("updateById", new UpdateByIdAmi());
        baseMapperInvoke.put("updateTemplateById", new UpdateTemplateByIdAmi());
        baseMapperInvoke.put("deleteById", new DeleteByIdAmi());
        baseMapperInvoke.put("unique", new UniqueAmi());
        baseMapperInvoke.put("single", new SingleAmi());
        baseMapperInvoke.put("all", new AllAmi());
        baseMapperInvoke.put("allCount", new AllCountAmi());
        baseMapperInvoke.put("template", new TemplateAmi());
        baseMapperInvoke.put("templateOne", new TemplateOneAmi());
        baseMapperInvoke.put("templateCount", new TemplateCountAmi());
        baseMapperInvoke.put("updateByIdBatch", new UpdateByIdBatchAmi());
        baseMapperInvoke.put("execute", new ExecuteAmi());
        baseMapperInvoke.put("executeUpdate", new ExecuteUpdateAmi());
        baseMapperInvoke.put("insertBatch", new InsertBatchAmi());
        baseMapperInvoke.put("getSQLManager", new GetSQLManagerAmi());
        baseMapperInvoke.put("insertTemplate", new InsertTemplateAmi());

        // beetlsql内置的基接口, 使用 InnerMapperInvoke 处理.(为了不改变原有代码, 将来推荐统一使用AmiInnerProxyMapperInvoke).
        mapperProxyInvoke.put(BaseMapper.class, baseMapperInvoke);
        


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

    /**
     * 添加接口与代理的映射
     *
     * @param c                 一般填写接口
     * @param mapperInvokeProxy 如果是自定义基接口, 一般对应 {@link AmiInnerProxyMapperInvoke} 即可高度扩展. 也可以自定义一个proxy
     */
    public void setMapperInvokeProxy(Class c, String method,MapperInvoke mapperInvokeProxy) {
    		Map<String,MapperInvoke> map = mapperProxyInvoke.get(c);
    		if(map==null){
    			map = new HashMap<String,MapperInvoke>();
    			mapperProxyInvoke.put(c,map);
    		}
    		map.put(method, mapperInvokeProxy);
    		
    }

   public MapperInvoke getMapperInvokeProxy(Class c,String method){
	   Map<String,MapperInvoke> map =  mapperProxyInvoke.get(c);
	   if(map==null){
		   return null;
	   }else{
		   MapperInvoke invoke = map.get(method);
		   return invoke;
	   }
   }

    public void build() {
    		mapperConfig.methodDescBuilder = this.getMethodDescBuilder();
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
