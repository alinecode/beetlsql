package org.beetl.sql.core;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by woate on 2016/4/29.
 * JPA命名转换器提供表名，字段名的映射
 */
@Deprecated
public class JPANameConversion extends NameConversion {
    static final Map<Class, Map<String, String>> PROP2COL_CACHE = new ConcurrentHashMap<Class, Map<String, String>>();
    static final Map<Class, Map<String, String>> COL2PROP_CACHE = new ConcurrentHashMap<Class, Map<String, String>>();
    @Override
    public String getTableName(Class<?> c) {
        Table table = c.getAnnotation(Table.class);
        return table.name();
    }

    @Override
    public String getColName(Class<?> c, String attrName) {
        init(c);
        return PROP2COL_CACHE.get(c).get(attrName);
    }

    @Override
    public String getPropertyName(Class<?> c, String colName) {
        init(c);
        return COL2PROP_CACHE.get(c).get(colName);
    }
    void init(Class<?> c) {
    		if(COL2PROP_CACHE.containsKey(c)) return ;
    		synchronized(c){
    			if(COL2PROP_CACHE.containsKey(c)) return ;
    			Field[] fields = c.getDeclaredFields();
                Map<String, String> cols = PROP2COL_CACHE.get(c);
                Map<String, String> props = COL2PROP_CACHE.get(c);
                if(cols == null){
                    cols = new ConcurrentHashMap<String, String>();
                    for (Field field : fields) {
                        Column column = field.getAnnotation(Column.class);
                        if (column != null) {
                            cols.put(field.getName(), column.name());
                        }
                    }
                    PROP2COL_CACHE.put(c, cols);
                }
                if(props == null){
                    props = new ConcurrentHashMap<String, String>();
                    for (Field field : fields) {
                        Column column = field.getAnnotation(Column.class);
                        if (column != null) {
                            props.put(column.name(), field.getName());
                        }
                    }
                    COL2PROP_CACHE.put(c, props);
                }
    		}
    		
    		
        
    }

}
