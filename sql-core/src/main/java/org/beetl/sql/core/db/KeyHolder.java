package org.beetl.sql.core.db;

import org.beetl.sql.clazz.ClassAnnotation;
import org.beetl.sql.clazz.kit.BeanKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static KeyHolder empty = new KeyHolder(new String[0]);

    protected KeyHolder() {

    }

    public KeyHolder(String[] attrNames) {
        this.attrNames = attrNames;
    }

    public KeyHolder(String attrName) {
        this.attrNames = new String[]{attrName};
    }

    public int getInt(String attrName) {
        int index = findIndex(attrName);
        return ((Number) values[index]).intValue();
    }

    /**
     * 从class定义中获取数据库返回的值
     *
     * @param c
     * @return
     */
    public static KeyHolder getKeyHolderByClass(Object obj) {
    	if(obj instanceof Map){
    		return KeyHolder.empty;
		}
    	Class c = obj.getClass();
        ClassAnnotation annotation = ClassAnnotation.getClassAnnotation(c);
        String[] attrs = annotation.getInsertAutoAttrs();
        if(attrs.length==0){
			return KeyHolder.empty;
		}
        List<String> list  = new ArrayList();;
        for(String attr : attrs){
			Object o = BeanKit.getBeanProperty(obj,attr);
			if(o==null){
				list.add(attr);
			}
		}

        return attrs.length==list.size()?new KeyHolder(attrs):new KeyHolder(list.toArray(new String[0]));
    }

    public Long getLong(String attrName) {
        int index = findIndex(attrName);
        return values[index] == null ? null : ((Number) values[index]).longValue();
    }

    public String getString(String attrName) {
        int index = findIndex(attrName);
        return values[index] == null ? null : values[index].toString();
    }

    public Object getObject(String attrName) {
        int index = findIndex(attrName);
        return values[index] == null ? null : values[index];
    }

    private int findIndex(String attrName) {
        if (attrNames.length == 1 && attrNames[0].equals(attrName)) {
            return 0;
        }
        for (int i = 0; i < attrNames.length; i++) {
            if (attrName.equalsIgnoreCase(attrNames[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("未找到属性" + attrName);
    }

    /**
     * 是否有自增主健
     *
     * @return
     */
    public boolean hasAttr() {
        return attrNames != null && attrNames.length != 0;
    }

    public String[] getAttrNames() {
        return this.attrNames;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

	@Override
	public String toString() {
		return "KeyHolder{" + "values=" + Arrays.toString(values) + '}';
	}
}
