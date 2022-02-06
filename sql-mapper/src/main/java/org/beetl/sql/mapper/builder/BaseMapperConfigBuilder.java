package org.beetl.sql.mapper.builder;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.AutoMapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 入口类，自定义基接口配置构建器.使用MapperMethodParser 解析方法，得出采用的IAM类
 *
 *
 * @author xiandafu,luoyizhu@gmail.com
 */
@Plugin
public  class BaseMapperConfigBuilder implements MapperConfigBuilder {

    /**
     * 用户添加自定义方法
	 * 或者提供给其他自定义的BaseMapper使用
	 * @see #addMapperClass(Class)
	 */
	protected final Map<Method, MapperInvoke> amiMethodMap = new ConcurrentHashMap<>();


	public BaseMapperConfigBuilder() {
		init();
	}

	/**
	 * 解析调用的接口方法所对应的 MapperInvoke 类
	 *
	 * @param entity 接口实体类
	 * @param mapperClass BaseMapper接口或子接口
	 * @param method 调用的接口方法
	 *
	 * @return {@link MapperInvoke}
	 */
	@Override
	public MapperInvoke getAmi(Class entity, Class mapperClass, Method method) {
		MapperInvoke mapperInvoke = amiMethodMap.get(method);
		if (mapperInvoke != null) {
			return mapperInvoke;
		}
		MapperMethodParser mapperMethodParser = new MapperMethodParser(entity, mapperClass, method);
		mapperInvoke = mapperMethodParser.parse();
		mapperInvoke = wrap(mapperInvoke, method);
		amiMethodMap.putIfAbsent(method, mapperInvoke);
		return mapperInvoke;
    }


    /**
     * 添加一个baseMapper类，可以添加任意多的BaseMapper，比如有些Basemapper有crud，而有些只有查询
     */
    protected void init(){
        addMapperClass(BaseMapper.class);
    }

    @Override
    public void addMapperClass(Class c){
        scanBaseMapper(c);
    }



    protected void scanBaseMapper(Class c){
        HashMap<Method, MapperInvoke> map = new HashMap<>();
       Method[] methods =  c.getMethods();
       for(Method method:methods){
           AutoMapper autoMapper = method.getAnnotation(AutoMapper.class);
           if(autoMapper==null){
               continue;
           }
           Class mapperClass = autoMapper.value();
           MapperInvoke ins = (MapperInvoke)BeanKit.newSingleInstance(mapperClass);
           map.put(method,wrap(ins,method));

       }
       if(map.isEmpty()){
           return ;
       }
       this.amiMethodMap.putAll(map);
    }


    /**
     * 允许代理，可以对MapperInvoke做更多的控制
     * <pre>
     *     class MapperInvokeProxy  implements MapperInvoke{
     *         public MapperInvokeProxy(MapperInvoke old){};
     *         Object call(SQLManager sm,
     *              Class entityClass, Method m, Object[] args){
     *                  //做一些额外控制
     *                  return old.call(sm,entityClass,args);
     *              }
     *     }
     * </pre>
     * @param old
     * @return
     */
    protected  MapperInvoke wrap(MapperInvoke old,Method method){
        return old;
    }






}
