package org.beetl.sql.core.orm;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.StringKit;

public class MappingEntity {
	private String target;
	private boolean isSingle = false;
	private boolean isClassMapping = false;
	String tailAttrName;
	Map<String,String> mapkey;
	public void map(List list, SQLManager sm) {
		for(Object obj:list){
			if(isClassMapping){
				mapClassItem(obj,sm);
			}else{
				mapSqlItem(obj,sm);
			}
			
		}
		
	}
	private void mapSqlItem(Object obj,SQLManager sm){
		throw new RuntimeException();
	}
	
	private void mapClassItem(Object obj,SQLManager sm){
		
		 String fullName = obj.getClass().getPackage().getName()+"."+target;
		 Class cls  = getCls(fullName);
		 Object ins = getIns(cls);
		
		 for(Entry<String,String> entry:this.mapkey.entrySet()){
			 String attr = entry.getKey();
			 String targetAttr = entry.getValue();
			 Object value = getBeanProperty(obj,attr);
			 setBeanProperty(ins,value,targetAttr);
			 
		 }
		 
		 List ret = sm.template(ins);
		 
		 if(!this.isSingle){
			 setTailAttr(obj,ret.get(0));
			 
		 }else{
			 setTailAttr(obj,ret);
		 }
	}
	
	private Method getGetter(Object o,String attrName){
		String getter = "get"+StringKit.toUpperCaseFirstOne(attrName);
		try{
			Method m = o.getClass().getMethod(getter, new Class[]{});
			return m;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	private Object getBeanProperty(Object o,String attrName){
		
		try{
			Method m = getGetter(o,attrName);
			return m.invoke(o,new Object[0]);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	private void setBeanProperty(Object o,Object value,String attrName){
		//for simple 
		String setter = "set"+StringKit.toUpperCaseFirstOne(attrName);
		try{
			Method getter = getGetter(o,attrName);
			Method m = o.getClass().getMethod(setter,getter.getReturnType());
			m.invoke(o, value);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	
	
	private void setTailAttr(Object o,Object value){
		if(o instanceof Tail){
			((Tail)o).set(this.tailAttrName, value);
		}else{
			//annotation
			Method m = BeanKit.getTailMethod(o.getClass());
			if(m==null){
				throw new RuntimeException("must implement tail or use @tail");
			}
			try{
				m.invoke(o, tailAttrName,value);
			}catch(Exception ex){
				throw new RuntimeException(ex);
			}
			
		}
	}
	
	private Object getIns(Class cls){
		try{
			
			return cls.newInstance();
		}catch(Exception ex ){
			throw new RuntimeException(ex);
		}
		
	}
	private Class getCls(String fullName){
		Class cls = null;
		
		try{
		    cls = Class.forName(fullName);
			return cls;
		}catch(Exception ex){
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader!=null){
				try{
					cls = loader.loadClass(fullName);
					return cls;
				}catch(Exception e){
					throw new RuntimeException(e);
				}
				
			}else{
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
	public String getTailAttrName() {
		return tailAttrName;
	}

	public void setTailAttrName(String tailAttrName) {
		this.tailAttrName = tailAttrName;
	}
	
	
	
	public Map<String, String> getMapkey() {
		return mapkey;
	}

	public void setMapkey(Map<String, String> mapkey) {
		this.mapkey = mapkey;
	}

	public boolean isClassMapping() {
		return isClassMapping;
	}

	public void setClassMapping(boolean isClassMapping) {
		this.isClassMapping = isClassMapping;
	}
	
}
