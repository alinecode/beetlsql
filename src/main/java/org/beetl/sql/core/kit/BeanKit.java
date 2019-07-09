package org.beetl.sql.core.kit;


import org.beetl.core.fun.MethodInvoker;
import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.annotatoin.Builder;
import org.beetl.sql.core.annotatoin.Tail;
import org.beetl.sql.core.annotatoin.builder.AttributeBuilderHolder;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanKit {
    //目前没有合适放的地方，忽略
//	public static String BEETL_VERSION = "beetlsql";
//	static{
//		URL url = GroupTemplate.class.getResource("/org/beetl/core/beetl-default.properties");
//		if(url.getProtocol().equals("jar")){
//			String path = url.getPath();
//			int index = path.indexOf(".jar!");
//			int i = path.lastIndexOf("beetlsql-", index);
//			
//		}
//	}
	public static boolean  queryLambdasSupport = JavaType.isJdk8();

    private static final Map<Class, Method> tailBeans = new ConcurrentHashMap<Class, Method>();

    private static Method NULL = null;

    static {
        try {
            NULL = Object.class.getMethod("toString", new Class[]{});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static Method getTailMethod(Class type) {
        //如果实现了注解也行@Tail也行
        Method m = tailBeans.get(type);
        if (m != null) {
            if (m == NULL) {
                return null;
            } else {
                return m;
            }

        } else {
            Tail an = getTailAnnotation(type);
            if (an == null) {
                tailBeans.put(type, NULL);
                return null;
            } else {
                m = BeanKit.tailMethod(type, an.set());
                if (m == null) {
                    tailBeans.put(type, NULL);
                    return null;
                } else {
                    tailBeans.put(type, m);
                    return m;
                }

            }
        }


    }

    private static Tail getTailAnnotation(Class type) {
        if (Object.class.isAssignableFrom(type)) {
            Tail an = (Tail) type.getAnnotation(Tail.class);
            if (an != null) {
                return an;
            } else {
                Class parent = type.getSuperclass();
                if (parent == null) {
                    return null;
                }
                return getTailAnnotation(parent);
            }
        } else {
            return null;
        }

    }

    public static PropertyDescriptor[] propertyDescriptors(Class<?> c) throws IntrospectionException {

        BeanInfo beanInfo = null;
        beanInfo = Introspector.getBeanInfo(c);
        return beanInfo.getPropertyDescriptors();

    }

    public static List<Method> getterMethod(Class<?> c) {

        PropertyDescriptor[] ps;
        try {
            ps = propertyDescriptors(c);
        } catch (IntrospectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        List<Method> list = new ArrayList<Method>();
        for (PropertyDescriptor p : ps) {
            if (p.getReadMethod() != null && BeanKit.getWriteMethod(p, c) != null) {
                list.add(p.getReadMethod());
            }
        }
        return list;


    }

    public static Method tailMethod(Class type, String name) {
        try {
            Method m = type.getMethod(name, new Class[]{String.class, Object.class});
            return m;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    public static Map getMapIns(Class cls) {
        if (cls == Map.class) {
            return new CaseInsensitiveHashMap();
        } else {
            try {
                return (Map) cls.newInstance();
            } catch (Exception e) {
                return null;
            }
        }


    }

    public static List getListIns(Class list) {
        if (list == List.class) {
            return new ArrayList();
        } else {
            try {
                return (List) list.newInstance();
            } catch (Exception e) {
                return null;
            }
        }

    }


    public static Object getBeanProperty(Object o, String attrName) {

        try {
            MethodInvoker inv = ObjectUtil.getInvokder(o.getClass(), attrName);
            return inv.get(o);
        } catch (Exception ex) {
            throw new RuntimeException("POJO属性访问出错:" + attrName, ex);
        }
    }
    
    public static MethodInvoker getMethodInvokerProperty(Object o, String attrName) {

    	return ObjectUtil.getInvokder(o.getClass(), attrName);
    }

    public static void setBeanProperty(Object o, Object value, String attrName) {

        MethodInvoker inv = ObjectUtil.getInvokder(o.getClass(), attrName);
        inv.set(o, value);

    }

    public static Object convertValueToRequiredType(Object result, Class<?> requiredType) {
        if (result == null) {
            return null;
        }
        Class type = result.getClass();
        if (type == result) {
            //大多数情况，都是这样
            return result;
        }
        if (String.class == requiredType) {
            return result.toString();
        }
        //判断Number对象所表示的类或接口是否与requiredType所表示的类或接口是否相同，或者是否是其超类或者超接口
        else if (Number.class.isAssignableFrom(requiredType)) {
            if (result instanceof Number) {
                return NumberKit.convertNumberToTargetClass((Number) result, (Class<Number>) requiredType);
            } else {
                return NumberKit.parseNumber(result.toString(), (Class<Number>) requiredType);
            }
        } else if (requiredType.isPrimitive()) {
            if (result instanceof Number) {
                return NumberKit.convertNumberToTargetClass((Number) result, requiredType);
            }
        }

        throw new IllegalArgumentException("无法转化成期望类型:" + requiredType);
    }


    public static Annotation getAnnotation(Class c, Class expect) {
        do {
            Annotation an = c.getAnnotation(expect);
            if (an != null) {
                return an;
            }
            c = c.getSuperclass();
        } while (c != null);
        return null;

    }


    public static AttributeBuilderHolder getAttributeHanlderHolder(Class c, String property, PropertyDescriptor p){
        Method m = p.getReadMethod();

        Annotation[] ans = m.getAnnotations();
        AttributeBuilderHolder holder = checkAttributeHanlderHolder(ans);
        if(holder!=null){
            return holder;
        }
        try {
            Field f =c.getDeclaredField(property);
            ans =  f.getDeclaredAnnotations();
            holder =  checkAttributeHanlderHolder(ans);
        } catch (NoSuchFieldException e) {
            //忽略
        }
        return holder;

    }

    private static AttributeBuilderHolder checkAttributeHanlderHolder(Annotation[] ans){
        for(Annotation an:ans){
            Builder builder =an.annotationType().getAnnotation(Builder.class);
            if(builder ==null){
               continue;
            }

            Class clz = builder.value();
            AttributeBuilderHolder holder = new AttributeBuilderHolder(an, builder);
            return holder;

        }
        return null;

    }

    public  static <T> T newInstance(Class<T> c){

        try {

            return c.newInstance();

        } catch (InstantiationException e) {
            throw new BeetlSQLException(BeetlSQLException.OBJECT_INSTANCE_ERROR,e);

        } catch (IllegalAccessException e) {
            throw new BeetlSQLException(BeetlSQLException.OBJECT_INSTANCE_ERROR,e);
        }

    }


    public static <T extends Annotation> T getAnnoation(Class c, String property, Method getter, Class<T> annotationClass) {
        T t = getter.getAnnotation(annotationClass);
        if (t != null) {
            return t;
        } else {
            try {
				while (c != null) {
					Field[] fs = c.getDeclaredFields();
					for (Field f : fs) {
						if (!f.getName().equals(property)) {
							continue;
						}
						t = f.getAnnotation(annotationClass);
						return t;

					}
					c = c.getSuperclass();
				}
                return t;
            } catch (Exception e) {
                return null;
            }

        }
    }

    public static <T extends Annotation> T getAnnoation(Class c, String property, Class<T> annotationClass) {
        MethodInvoker invoker = ObjectUtil.getInvokder(c, property);
        if (invoker == null) {
            return null;
        }

        Method getter = invoker.getMethod();
        return getAnnoation(c, property, getter, annotationClass);

    }





    public static List<Annotation> getAllAnnoation(Class c, String property) {
        MethodInvoker invoker = ObjectUtil.getInvokder(c, property);
        if (invoker == null) {
            return null;
        }

        Method getter = invoker.getMethod();
        Annotation[] array1 = getter.getAnnotations();
        Annotation[] array2 = null;
        Field f = getField(c, property);
        if (f != null) {
            array2 = f.getAnnotations();
        }

        return addAnnotation(array1, array2);

    }

    /**
     * 根据Class 和 property 获取自身或父类的 Field
     *
     * @param c
     * @param property
     * @return
     */
    private static Field getField(Class c, String property) {
        Field field = null;
        if (c != null) {
            try {
                field = c.getDeclaredField(property);
            } catch (Exception e) {
                //当前Class获取不到时尝试从父类中获取
                field = getField(c.getSuperclass(), property);
            }
        }
        return field;
    }

    private static List<Annotation> addAnnotation(Annotation[] array1, Annotation[] array2) {
        List<Annotation> list = new ArrayList<Annotation>();
        if (array1 != null) {
            list.addAll(Arrays.asList(array1));
        }

        if (array2 != null) {
            list.addAll(Arrays.asList(array2));
        }
        return list;

    }

    /**
     * 获取prop的setter方法
     *
     * @param prop
     * @param type
     * @return
     */
    public static Method getWriteMethod(PropertyDescriptor prop, Class<?> type) {
        Method writeMethod = prop.getWriteMethod();
        //当使用lombok等链式编程方式时 有返回值的setter不被认为是writeMethod，需要自己去获取
        if (writeMethod == null && !"class".equals(prop.getName())) {
            String propName = prop.getName();
            //符合JavaBean规范的set方法名称（userName=>setUserName,uName=>setuName）
            String setMethodName = "set" + (propName.length() > 1 && propName.charAt(1) >= 'A' && propName.charAt(1) <= 'Z' ? propName : StringKit.toUpperCaseFirstOne(propName));
            try {
                writeMethod = type.getMethod(setMethodName, prop.getPropertyType());
            } catch (Exception e) {
                //不存在set方法
                return null;
            }
        }
        return writeMethod;
    }

   
    public static String getPackageName(Class<?> clazz) {
        return StringKit.beforeLast(clazz.getName(), '.');
    }

    private static class Foo {}

    public static void main(String[] args) {
        System.out.println(getPackageName(Foo.class));
    }

}
