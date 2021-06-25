package org.beetl.sql.core.extend;

import org.beetl.sql.clazz.EnumKit;

public class EnumExtend {
    public  Enum getEnumByValue(Class c, Object value) {
        return EnumKit.getEnumByValue(c,value);
    }

    public Object getValueByEnum(Object en) {
        return EnumKit.getValueByEnum(en);
    }


}
