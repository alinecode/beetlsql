package org.beetl.sql.core.mapper.builder;

import static org.beetl.sql.core.mapper.builder.MapperConfigBuilder.*;

import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.MethodDesc;

/**
 * <pre>
 * 用来处理mapper接口里的方法对应的处理类
 * </pre>
 * create time : 2017-04-27 15:27
 *
 * @author luoyizhu@gmail.com,xiandafu
 */
public final class MapperConfig {
   
    MapperConfigBuilder seniorConfigBuilder = null;
    MethodDescBuilder methodDescBuilder = null;

    public  MapperConfig() {
    		seniorConfigBuilder = new MapperConfigBuilder(this);
    		methodDescBuilder = seniorConfigBuilder.getMethodDescBuilder();
    }

    /**
     * @return 配置构建器
     */
    public MapperConfigBuilder getBuilder() {
        return seniorConfigBuilder;
    }

    public MethodDesc createMethodDesc() {
        return methodDescBuilder.create();
    }

    public MapperInvoke getMapperInvokeProxy(Class c,String method) {
        return seniorConfigBuilder.getMapperInvokeProxy(c, method);
    }



    public MapperInvoke getMethodDescProxy(int methodDescType) {
        return seniorConfigBuilder.METHOD_DESC_PROXY_ARRAY[methodDescType];
    }

}
