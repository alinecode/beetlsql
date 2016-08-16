package org.beetl.sql.core.orm;

import java.util.List;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.db.TableDesc;

public class MappingEntity extends Mapping {
	private String targetTable;
	private boolean isSingle = false;
	@Override
	public void map(List list, SQLManager sm) {
		for(Object obj:list){
			mapItem(obj,sm);
		}
		
	}
	
	private void mapItem(Object obj,SQLManager sm){
		 TableDesc table = sm.getMetaDataManager().getTable(targetTable);
		 String className = sm.getNc().getClassName(targetTable);
		 String fullName = obj.getClass().getPackage().getName()+"."+className;
		 Class cls  = getCls(fullName);
		 if(this.isSingle){
			 Object o =sm.single(cls, mappingKey.getValue(obj, sm));
			 
		 }
	}
	
	private void setTailAttr(Object o){
		if(o instanceof Tail){
			((Tail)o).set(this.tailAttrName, o);
		}else{
			//annotation
			throw new UnsupportedOperationException();
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
					return null;
				}
				
			}else{
				return null;
			}
		}
	}
	public String getTargetTable() {
		return targetTable;
	}
	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}
	public boolean isSingle() {
		return isSingle;
	}
	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
	}
	
	
}
