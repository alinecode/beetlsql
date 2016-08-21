package org.beetl.sql.core.orm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.core.om.MethodInvoker;
import org.beetl.core.om.ObjectUtil;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.StringKit;


/**
 * 实现关系映射
 * @author xiandafu
 *
 */
public class MappingEntity {
	private String target;
	private boolean isSingle = false;
	Map<String, String> mapkey;
	String sqlId = null;

	//
	String tailName;
	boolean absentPackage = false;
	Class targetClass = null;
	
	Map<String, Method> setMethod = new HashMap<String,Method>();

	public void map(List list, SQLManager sm) {
		if(list.size()==0){
			return ;
		}
		init(list.get(0));
		for (Object obj : list) {
			mapClassItem(obj, sm);

		}

	}

	private void init(Object obj) {
		if (target.indexOf(".") == -1) {
			// 参数不带包名
			this.tailName = StringKit.toLowerCaseFirstOne(target);
			absentPackage = true;
		} else {
			int index = target.lastIndexOf(".");
			String className = target.substring(index+1);
			this.tailName = StringKit.toLowerCaseFirstOne(className);

		}
	}



	private void mapClassItem(Object obj, SQLManager sm) {
		if (targetClass == null) {
			String fullName = absentPackage ? obj.getClass().getPackage().getName() + "." + target : target;
			targetClass = getCls(fullName);
		}

	
		List ret = null;
		if (sqlId != null) {
			Map<String,Object> paras = new HashMap<String,Object>();
			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = getBeanProperty(obj, attr);
				paras.put(targetAttr, value);

			}
			ret = sm.select(sqlId, targetClass, paras);
		} else {
			
			Object ins = getIns(targetClass);
			for (Entry<String, String> entry : this.mapkey.entrySet()) {
				String attr = entry.getKey();
				String targetAttr = entry.getValue();
				Object value = getBeanProperty(obj, attr);
				setBeanProperty(ins, value, targetAttr);

			}
			ret = sm.template(ins);
		}

		if (!this.isSingle) {
			if(ret.isEmpty()){
				setTailAttr(obj, null);
			}else{
				setTailAttr(obj, ret.get(0));
			}
			

		} else {
			
			setTailAttr(obj, ret);
		}
	}



	private Object getBeanProperty(Object o, String attrName) {

		try {
			MethodInvoker inv = ObjectUtil.getInvokder(o.getClass(), attrName);
			return inv.get(o);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void setBeanProperty(Object o, Object value, String attrName) {
		Method m = setMethod.get(attrName);
		if(m==null){
			try {
				Class t = o.getClass();
				MethodInvoker inv = ObjectUtil.getInvokder(t, attrName);
				if(inv==null){
					throw new BeetlSQLException(BeetlSQLException.ORM_ERROR,"映射为找到属性"+attrName+" in "+t);
				}
				String getterName = inv.getMethod().getName();
				String setterName = "s"+getterName.substring(1);
				m = t.getMethod(setterName, inv.getReturnType());
				setMethod.put(attrName, m);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		try{
			m.invoke(o, value);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
		
		
	}

	private void setTailAttr(Object o, Object value) {
		if (o instanceof Tail) {
			((Tail) o).set(tailName, value);
		} else {
			// annotation
			Method m = BeanKit.getTailMethod(o.getClass());
			if (m == null) {
				throw new RuntimeException("must implement tail or use @tail");
			}
			try {
				m.invoke(o, tailName, value);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

		}
	}

	private Object getIns(Class cls) {
		try {

			return cls.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	private Class getCls(String fullName) {
		Class cls = null;

		try {
			cls = Class.forName(fullName);
			return cls;
		} catch (Exception ex) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader != null) {
				try {
					cls = loader.loadClass(fullName);
					return cls;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			} else {
				throw new RuntimeException(ex);
			}
		}
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {

		this.target = target;
	}

	public boolean isSingle() {
		return isSingle;
	}

	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}

	public Map<String, String> getMapkey() {
		return mapkey;
	}

	public void setMapkey(Map<String, String> mapkey) {
		this.mapkey = mapkey;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

}
