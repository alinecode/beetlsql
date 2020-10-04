package org.beetl.sql.core;
/**
 *  可以设置pojo的额外属性和关系映射，需要主要的是，value有可能是LazyEntity，因此在实现get方法的时候，必须判断
 *  是否是LazyEnity。如果是，还需要调用LazyEnity.get获取，具体参考TailBean
 * @author xiandafu
 *
 */
public interface Tail extends java.io.Serializable {
	public Object get(String key);
	public void set(String key,Object value);
}
