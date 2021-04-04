package org.beetl.sql.clazz.kit;


import lombok.Data;
import org.beetl.core.GroupTemplate;
import org.beetl.core.fun.MethodInvoker;
import org.beetl.core.fun.ObjectUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean处理小工具
 * @author xiandafu
 */
public class BeanKit {

	public static boolean  queryLambdasSupport = JavaType.isJdk8();

    private static final Map<Class, Method> tailBeans = new ConcurrentHashMap<Class, Method>();

    private static Method NULL = null;

    public static String[] EMP_STRING_ARRAY  = new String[0];
    public static Object[] EMP_OBJECT_ARRAY  = new Object[0];

    static ClassLoader classLoader = Thread.currentThread().getContextClassLoader() != null ? Thread.currentThread().getContextClassLoader() : GroupTemplate .class.getClassLoader();

    static {
        try {
            NULL = Object.class.getMethod("toString", new Class[]{});
        } catch (Exception e) {
            // 可能发生
			throw new IllegalStateException(e);
        }
    }

	/**
	 * 一个通常的class对应的实例的缓存
	 */
    public static Cache classInsCache = new DefaultCache();

    public static PropertyDescriptor[] propertyDescriptors(Class<?> c) throws IntrospectionException {

        BeanInfo beanInfo = null;
        beanInfo = Introspector.getBeanInfo(c);
        return beanInfo.getPropertyDescriptors();

    }

    public static PropertyDescriptor getPropertyDescriptor(Class c,String attr) {
        try{
            PropertyDescriptor[] ps = propertyDescriptors(c);
            for(PropertyDescriptor p:ps){
                if(p.getName().equals(attr)){
                    return p;
                }
            }
            return null;
        }catch(IntrospectionException ex){
            throw new IllegalStateException("期望 "+c+"遵循Bean规范，不能获取属性 "+attr+" 定义");
        }

    }


    /**
     * 如果在映射结果指定为Map.class, 返回一个大小写不敏感的map子类，否则按照用户指定的map映射
     * @param cls
     * @return
     */
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




	/**
	 * 调用Beetl，设置某个属性值
	 * @param o
	 * @param value
	 * @param attrName
	 */
    public static void setBeanProperty(Object o, Object value, String attrName) {

        MethodInvoker inv = ObjectUtil.getInvokder(o.getClass(), attrName);
        if(inv== null){
            throw new IllegalArgumentException("未能找到对象" +o.getClass()+"的属性");
        }
        inv.set(o, value);

    }

	/**
	 * 调用Beetl，设置某个属性，如果属性类型与值不配置，试图转化
	 * @param o
	 * @param value
	 * @param attrName
	 */
	public static void setBeanPropertyWithCast(Object o, Object value, String attrName){
		if(value==null){
			return ;
		}
		MethodInvoker inv = ObjectUtil.getInvokder(o.getClass(), attrName);
		Class type = inv.getReturnType();
		Class valueType = value.getClass();
		Object requiredValue = convertValueToRequiredType(value,type);
		inv.set(o,requiredValue);


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



        //TODO,增加一个扩展点，支持其他类型，比如JDK时间，Blob，Clob等


        throw new IllegalArgumentException("无法转化成期望类型:" + requiredType);
    }


	/**
	 * 得到某个类的类注解，如果没有，则寻找父类
	 * @param c
	 * @param expect
	 * @return
	 */
    public static  <T extends Annotation> T getAnnotation(Class c, Class<T> expect) {
        do {
            T an = (T) c.getAnnotation(expect);
            if (an != null) {
                return an;
            }
            c = c.getSuperclass();
        } while (c != null&&c!=Object.class);
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

    public  static Collection newCollectionInstance(Class c){
       if(c.isInterface()){
           if(c.isAssignableFrom(List.class)){
               return new ArrayList();
           }else if(c.isAssignableFrom(Set.class)){
               return new HashSet();
           }else{
               throw new UnsupportedOperationException("不支持的类 "+c);
           }
       }else{
           return (Collection)newInstance(c);
       }
    }

    /**
     * 创建一个对象的单例
     * @param c
     * @param <T>
     * @return
     */
	public  static <T> T newSingleInstance(Class<T> c){
	    synchronized (c){
            Object ins = classInsCache.get(c);
            if(ins!=null){
                return (T)ins;
            }
            ins =  newInstance(c);
            classInsCache.put(c,ins);
            return (T)ins;
        }

	}


    public static <T extends Annotation> T getAnnotation(Class c, String property, Method getter, Class<T> annotationClass) {
        if(getter==null){
        	throw new NullPointerException("期望POJO类符合javabean规范，"+c+" 没有getter方法");
		}
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

    public static <T extends Annotation> T getAnnotation(Class c, String property, Class<T> annotationClass) {
        MethodInvoker invoker = ObjectUtil.getInvokder(c, property);
        if (invoker == null) {
            return null;
        }

        Method getter = invoker.getMethod();
        return getAnnotation(c, property, getter, annotationClass);

    }

    public static List<Annotation> getAllAnnotation(Class c, String property) {
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
    public static Field getField(Class c, String property) {
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



    public static  boolean isJavaClass(Class entityClass){
        Package pck = entityClass.getPackage();
        if(pck==null){
            return false;
        }
        String pkgName = entityClass.getPackage().getName();
        if(pkgName.startsWith("java")){
            return true;
        }
        return false;
    }

	public static boolean isBaseDataType(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return true;
		}

		if (clazz.getName().startsWith("java")) {
			return ((clazz == String.class) || clazz == Integer.class || clazz == Byte.class
					|| clazz == Long.class || clazz == Double.class || clazz == Float.class
					|| clazz == Character.class || clazz == Short.class || clazz == BigDecimal.class
					|| clazz == BigInteger.class || clazz == Boolean.class || clazz == java.util.Date.class
					|| clazz == java.sql.Date.class || clazz == java.sql.Timestamp.class
                    || clazz==java.time.LocalDateTime.class ||clazz==java.time.LocalDate.class);
		} else {
			return false;
		}

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

    public static  boolean containViewType(Class[] cls, Class viewType){
        if(cls.length==1){
            return cls[0]==viewType;
        }else if(cls.length==2){
            return cls[0]==viewType||cls[1]==viewType;
        }

        for(Class z:cls){
            if(z==viewType){
                return true;
            }
        }
        return false;
    }


    public static Class getCollectionType(Type type){
        if(!(type instanceof ParameterizedType) ){
            return null;
        }
        Class paraType =  getParameterTypeClass(type);
        return paraType;
    }

	public static Class[] getMapType(Type type){
		if(!(type instanceof ParameterizedType) ){
			return null;
		}
		Class[]  paraType =  getMapParameterTypeClass(type);
		return paraType;
	}

    public static Class getParameterTypeClass(Type t) {
        if (t instanceof WildcardType || t instanceof TypeVariable) {
            // 丢失类型
            return null;
        } else if (t instanceof ParameterizedType) {
            Type[] types =  ((ParameterizedType) t).getActualTypeArguments();
            if(types.length==0){
                return null;
            }
            Type type = types[0];
            if(type  instanceof ParameterizedType){
                return (Class)((ParameterizedType) type).getRawType();
            }else if(type instanceof  Class){
                return (Class)types[0];
            }else if(type instanceof  TypeVariable){
                //未定义，则返回null。则通过mapper接口，而不是方法来判断范型类型
                return null;
            }
            else {
                throw new UnsupportedOperationException(type.toString());
            }

        } else {
            throw new UnsupportedOperationException(t.toString());
        }

    }

	public static Class[] getMapParameterTypeClass(Type t) {
		if (t instanceof WildcardType || t instanceof TypeVariable) {
			// 丢失类型
			return null;
		} else if (t instanceof ParameterizedType) {
			Type[] types =  ((ParameterizedType) t).getActualTypeArguments();
			if(types.length==0){
				return null;
			}
			Class[] classTypes=new Class[2];
			Type type = types[0];
			if(type  instanceof ParameterizedType){
				classTypes[0] = (Class)((ParameterizedType) type).getRawType();
			}else if(type instanceof  Class){
				classTypes[0] = (Class)types[0];
			}else{
				throw new UnsupportedOperationException(type.toString());
			}

			Type type1 = types[1];
			if(type1  instanceof ParameterizedType){
				classTypes[1] = (Class)((ParameterizedType) type1).getRawType();
			}else if(type1 instanceof  Class){
				classTypes[1] = (Class)types[1];
			}else{
				throw new UnsupportedOperationException(type.toString());
			}

			return classTypes;

		} else {
			throw new UnsupportedOperationException(t.toString());
		}

	}


    public static Class getMapperEntity(Class mapperInterface){
        if (mapperInterface.isInterface()) {
            Type[] faces = mapperInterface.getGenericInterfaces();
            if (faces.length > 0 && faces[0] instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) faces[0];
                if (pt.getActualTypeArguments().length > 0) {
                    Class entityClass = (Class<?>) pt.getActualTypeArguments()[0];
                    return entityClass;
                }
            }
        }
       return null;
    }




    public static void main(String[] args) throws  Exception {
		Class c = User.class;
		Method m = c.getMethod("getMaps",new Class[0]);
		Type type = m.getGenericReturnType();
		Class[] tt = BeanKit.getMapParameterTypeClass(type);
		System.out.println(tt);

    }

    @Data
    public static class User{
		Map<String,Integer> maps = null;
	}



}
