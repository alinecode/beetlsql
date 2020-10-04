package org.beetl.sql.clazz.kit;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 一个Query和concat公用的类，获取lambda方法的名称
 * <pre>
 *     // attrName is userAge;
 *     String attrName = FunctionCache.toName(User::getUserAge);
 *
 * </pre>
 * @author xiandafu
 */
public class FunctionCache {
    static Cache<Function,String> cache = new DefaultCache<>();
    public static String getName(Function function){
        String name = cache.get(function);
        if(name==null){
            name = getFunctionName(function);
            cache.putIfAbsent(function,name);
        }
        return name;
    }

    private static String getFunctionName(Function function) {
        try {
            Method declaredMethod = function.getClass().getDeclaredMethod("writeReplace");
            declaredMethod.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) declaredMethod.invoke(function);
            String method = serializedLambda.getImplMethodName();
            String attr = null;
            if (method.startsWith("get")) {
                attr = method.substring(3);
            } else {
                attr = method.substring(2);
            }
            return attr;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

    }


}
