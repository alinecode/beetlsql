package org.beetl.sql.core.query.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author GavinKing
 * @ClassName: ObjectUtil
 * @Description: 属性工具
 * @date 2019/8/26
 */
public class FieldsUtil {

    public static List<Field> getAllFields(Class tempClass){
        List<Field> fieldList = new ArrayList<>() ;
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {
            fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }
}
