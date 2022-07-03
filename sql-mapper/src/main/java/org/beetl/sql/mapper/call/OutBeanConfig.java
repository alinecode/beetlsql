package org.beetl.sql.mapper.call;

import lombok.Data;
import org.beetl.sql.clazz.kit.BeanKit;

import java.util.HashMap;
import java.util.Map;

@Data
public class OutBeanConfig {
	Class bean;
	/**
	 * 存储过程参数位置，以及属性名
	 */
	Map<Integer,String> indexMap = new HashMap<>();
	Map<Integer,Class> indexTypeMap = new HashMap<>();
	/*在 mapper方法中的位置*/
	int paramIndex;

	public Object create(){
		return BeanKit.newInstance(bean);
	}

	public void setValue(Object bean,String name,Object value){
		BeanKit.setBeanProperty(bean,value,name);
	}


}
