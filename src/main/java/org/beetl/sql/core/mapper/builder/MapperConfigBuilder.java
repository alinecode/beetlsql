package org.beetl.sql.core.mapper.builder;

import org.beetl.sql.core.mapper.MapperInvoke;
import org.beetl.sql.core.mapper.MethodDesc;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 自定义基接口配置构建器.
 *
 * 灵活配置构建器, 满足喜欢捣腾的用户
 * 理论上这个类该写在 {@link MapperConfig} 文件里.
 * 但为了清晰理解代码, 独立了出来.
 * </pre>
 * create time : 2017-04-27 15:28
 *
 * @author luoyizhu@gmail.com,xiandafu
 */
public final class MapperConfigBuilder {

    /**
     * 用户添加自定义方法
     * 或者提供给其他自定义的BaseMapper使用
     */
    final Map<String, MapperInvoke> amiMethodMap = new HashMap<String, MapperInvoke>();

    /** 默认实现 */
    MethodDescBuilder methodDescBuilder = new MethodDescBuilder() {
        @Override
        public MethodDesc create() {
            return new MethodDesc();
        }
    };

    public MapperConfigBuilder() {
        // 直接内置BaseMapper的所有方法, 用户只需要定义与BaseMapper相同的方法名就可以使用
        amiMethodMap.putAll(MapperInvokeDataConfig.INTERNAL_AMI_METHOD);
    }

    /**
     * 获取方法对应的 Ami 处理类
     *
     * @param methodName 方法名
     * @return Ami处理类
     */
    public MapperInvoke getAmi(String methodName) {
        return this.amiMethodMap.get(methodName);
    }

    /**
     * @param methodDescBuilder 自定义创建 MethodDesc
     * @return this
     */
    public MapperConfigBuilder setMethodDescBuilder(MethodDescBuilder methodDescBuilder) {
        if (methodDescBuilder == null) {
            return this;
        }
        this.methodDescBuilder = methodDescBuilder;
        return this;
    }

    /**
     * <pre>
     * Ami: 全名 ApiMapperInvoke, 由于感觉这个名字太长所以简写了.
     *
     * 此方法用户可以给自定义的基接口扩展方法.
     *
     * 里面已经内置BaseMapper的所有方法, 用户只需要在自定义的基接口上定义与BaseMapper相同的方法名就可以使用
     * </pre>
     * <pre>
     * 假设你定义了一个基接口名字: MyMapper
     * 示例:
     * addAmi("selects", new AllAmi());
     * 在用户调用 MyMapper.selects(); 就能得到表的所有数据. 因为selects方法使用 AllAmi() 来处理.
     *
     *
     * 这个示例是查询表的所有id列表.(自定义Ami代码)
     * builder.addAmi("selectIds", new MapperInvoke() {
     *     public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
     *         String tableName = sm.getNc().getTableName(entityClass);
     *         TableDesc tableDesc = sm.getMetaDataManager().getTable(tableName);
     *         StringBuilder builder = new StringBuilder("select id from ").append(tableDesc.getName());
     *         return sm.execute(new SQLReady(builder.toString()), Integer.class);
     *     }
     * });
     * </pre>
     *
     * @param methodName 方法名 (自定义)
     * @param ami        MapperInvoke 处理该方法名的类
     * @return this
     */
    public MapperConfigBuilder addAmi(String methodName, MapperInvoke ami) {
        this.amiMethodMap.put(methodName, ami);
        return this;
    }

}
