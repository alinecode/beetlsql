package org.beetl.sql.core.mapper.builder;

import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.MethodDesc;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 用来处理mapper接口里的方法对应的处理类
 * </pre>
 * create time : 2017-04-27 15:27
 *
 * @author luoyizhu@gmail.com,xiandafu
 */
public final class MapperConfig {

    private final MapperConfigBuilder builder = new MapperConfigBuilder();
    private Map<Class, MapperConfigBuilder> mapperConfigBuilderMap = new HashMap<Class, MapperConfigBuilder>();

    public MapperConfig(Class c) {
        this.mapperConfigBuilderMap.put(c, builder);
        this.mapperConfigBuilderMap.put(BaseMapper.class, MapperInvokeDataConfig.BASE_MAPPER_BUILDER);
    }

    public MapperConfig() {
        this.mapperConfigBuilderMap.put(BaseMapper.class, MapperInvokeDataConfig.BASE_MAPPER_BUILDER);
    }

    public MethodDesc createMethodDesc() {
        return builder.methodDescBuilder.create();
    }

    public MapperConfigBuilder getBuilder() {
        return builder;
    }

    /**
     * 根据接口, 接口方法名, 获取对应的处理类
     *
     * @param c      接口
     * @param method 接口方法名
     * @return Ami 处理类
     */
    public MapperInvoke getAmi(Class c, String method) {
        MapperConfigBuilder builder = this.mapperConfigBuilderMap.get(c);
        if(builder==null){
        		return null;
        }else{
        		return builder.getAmi(method);
        }
        
    }

}
