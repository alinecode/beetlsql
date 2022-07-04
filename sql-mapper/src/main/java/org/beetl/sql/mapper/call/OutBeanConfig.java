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
	/**
	 * 存储过程的参数，以及对应的jdbc类型，可为空
	 */
	Map<Integer,Integer> indexJdbcMap = new HashMap<>();
	/**
	 * 存储过程的参数，以及对应的java类型
	 */
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
