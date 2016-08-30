package org.beetl.sql.core.orm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.beetl.core.om.MethodInvoker;
import org.beetl.core.om.ObjectUtil;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.db.ClassDesc;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
import org.beetl.sql.core.kit.StringKit;


/**
 * 实现关系映射,放入一个Lazy
 * @author xiandafu
 *
 */
public class LazyMappingEntity extends MappingEntity {
	
	public void map(List list, SQLManager sm) {
		if(list.size()==0){
			return ;
		}
		
		init(list.get(0));
		
		for (Object obj : list) {
			mapClassItem(obj, sm);

		}

	}



	protected void mapClassItem(final Object obj, final SQLManager sm) {
		
	    
		
		if (sqlId != null) {
			final Map<String,Object> paras = new HashMap<String,Object>();
			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = BeanKit.getBeanProperty(obj, attr);
				paras.put(targetAttr, value);
				
			}
			LazyEntity lazy = new LazyEntity(){

				@Override
				public Object get() {
					// TODO Auto-generated method stub
					List ret = sm.select(sqlId, targetClass, paras);
					return retValue(ret);
				}
				
			};
			setTailAttr(obj, lazy);
			
		} else {
			
			final Object ins = getIns(targetClass);
			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = BeanKit.getBeanProperty(obj, attr);
				BeanKit.setBeanProperty(ins, value, targetAttr);

			}
			LazyEntity lazy = new LazyEntity(){

				@Override
				public Object get() {
					// TODO Auto-generated method stub
					List ret = sm.template(ins);
					return retValue(ret);
				}
				
			};
			setTailAttr(obj, lazy);
			
		}

		
	}
	
	private Object retValue(List ret){
		if (isSingle) {
			if(ret.isEmpty()){
				return null;
			}else{
				return ret.get(0);
			}
			

		} else {
			
			return ret;
		}
	}


}
