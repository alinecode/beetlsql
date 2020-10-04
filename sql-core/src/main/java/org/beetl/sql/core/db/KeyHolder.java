package org.beetl.sql.core.db;

import org.beetl.sql.clazz.ClassAnnotation;

/**
 * 数据库在插入的时候，因为自增或者序列或者其他机制，自动生成数据，如下假设foo字段是触发器生成字段，插入后需要取回
 * <pre>
 *     KeyHolder holder = new KeyHolder(foo”)
 *     sqlManager.insert(sqlId,paras,holder);
 *     Long seq = holder.getLong("foo");
 *
 * </pre>
 */
public class KeyHolder {
	String[] attrNames = null;
	Object[] values = null;

	public static KeyHolder empty = new KeyHolder();
	protected KeyHolder(){

	}
	public KeyHolder(String[] attrNames){
		this.attrNames = attrNames;
	}
	public KeyHolder(String attrName){
		this.attrNames = new String[]{attrName};
	}

	public int getInt(String attrName){
		int index = findIndex(attrName);
		return  ((Number)values[index]).intValue();
	}

	/**
	 * 从class定义中获取数据库返回的值
	 * @param c
	 * @return
	 */
	public static KeyHolder getKeyHolderByClass(Class c){
		ClassAnnotation annotation = ClassAnnotation.getClassAnnotation(c);
		String[] attrs = annotation.getInsertAutoAttrs();
		return new KeyHolder(attrs);
	}
	
	public Long getLong(String attrName){
		int index = findIndex( attrName);
		return  values[index]==null?null:((Number)values[index]).longValue();
	}

	public String getString(String attrName){
		int index = findIndex( attrName);
		return  values[index]==null?null:((Object)values[index]).toString();
	}

	public Object getObject(String attrName){
		int index = findIndex( attrName);
		return  values[index]==null?null:((Object)values[index]);
	}

	private int findIndex(String attrName){
		if(attrNames.length==1&& attrNames[0].equals(attrName)){
			return 0;
		}
		for(int i = 0; i< attrNames.length; i++){
			if(attrName.equalsIgnoreCase(attrNames[i])){
				return i;
			}
		}
		throw new IllegalArgumentException("未找到属性"+attrName);
	}

	/**
	 * 是否有自增主健
	 * @return
	 */
	public boolean hasAttr(){
		return attrNames!=null&&attrNames.length!=0;
	}

	public String[] getAttrNames(){
		return this.attrNames;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}
}
