package org.beetl.sql.clazz.kit;


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
import java.util.function.Predicate;

/**
 * Bean处理小工具
 * @author xiandafu
 */
public class BeanKit {

	public static boolean queryLambdasSupport = JavaType.isJdk8();
	/**
	 * 设置false，支持lombok非javabean支持，链式调用
	 */
	public static boolean JAVABEAN_STRICT = true;
	static Map<Class,Map<String,Field>> classFields = new ConcurrentHashMap<>();
	static Map<Class,Map<String,PropertyDescriptorWrap>> classProperty = new ConcurrentHashMap<>();
	/**
	 * 一个通常的class对应的实例的缓存
	 */
	public static Cache classInsCache = new DefaultCache();

	public static PropertyDescriptorWrapFactory propertyDescriptorWrapFactory = (c, prop, i) -> new PropertyDescriptorWrap(c,prop,i);

	public static PropertyDescriptor[] propertyDescriptors(Class<?> c) throws IntrospectionException {

		BeanInfo beanInfo = null;
		beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getPropertyDescriptors();

	}

	public static Map<String,Field> getClassFields(Class c){
		Map<String,Field> map  = classFields.get(c);
		if(map!=null){
			return map;
		}
		HashMap fieldMap = JAVABEAN_STRICT?new HashMap():new CaseInsensitiveHashMap();
		for(Field field: c.getDeclaredFields()){
			String name = field.getName();
			fieldMap.put(name,field);
		}
		classFields.put(c,fieldMap);
		return fieldMap;

	}

	public static Map<String,PropertyDescriptorWrap> getClassProperty(Class c)  {
		Map<String,PropertyDescriptorWrap> map  =classProperty.get(c);
		if(map!=null){
			return map;
		}
		synchronized (c){
			map  =classProperty.get(c);
			if(map!=null){
				return map;
			}
			try{
				Map<String,PropertyDescriptorWrap> propertyMap = JAVABEAN_STRICT?new HashMap():new CaseInsensitiveHashMap();
				PropertyDescriptor[] propertyDescriptors = propertyDescriptors(c);
				for(int i=0;i<propertyDescriptors.length;i++){
					PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
					String name = propertyDescriptor.getName();
					propertyMap.put(name,propertyDescriptorWrapFactory.make(c,propertyDescriptor,i));
				}

				for(PropertyDescriptorWrap wrap:propertyMap.values()){
					wrap.init(c);
				}

				classProperty.put(c,propertyMap);
				return propertyMap;
			}catch (IntrospectionException ex){
				throw new IllegalStateException(c.getName());
			}
		}


	}

	public static PropertyDescriptorWrap getClassProperty(Class c,int index)  {
		Map<String,PropertyDescriptorWrap> map = getClassProperty(c);
		return map.values().stream().filter(new Predicate<PropertyDescriptorWrap>() {
			@Override
			public boolean test(PropertyDescriptorWrap o) {
				return o.i==index;
			}
		}).findFirst().get();

	}

	/**
	 * 清除缓存，只有在动态类加载场景下，才有可能需要这么做，如dcemv技术
	 */
	public static void clearCache(){
		classFields.clear();
		classProperty.clear();
		classInsCache.clearAll();
	}


	public static PropertyDescriptor getPropertyDescriptor(Class c, String attr) {
		Map<String,PropertyDescriptorWrap>  map = getClassProperty(c);
		PropertyDescriptorWrap propertyDescriptor =  map.get(attr);
		if(propertyDescriptor!=null){
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"找不到属性 "+attr+" @"+c);
		}
		return propertyDescriptor.getProp();
	}

	public static PropertyDescriptor getPropertyDescriptorWithNull(Class c, String attr) {
		Map<String,PropertyDescriptorWrap>  map = getClassProperty(c);

		PropertyDescriptorWrap propertyDescriptor =  map.get(attr);

		return propertyDescriptor.getProp();
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
			Map<String,PropertyDescriptorWrap> map =getClassProperty(o.getClass());
			PropertyDescriptorWrap propertyDescriptor = map.get(attrName);
			if(propertyDescriptor==null){
				throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"属性不存在 "+attrName+" @"+o.getClass());
			}
			return propertyDescriptor.getValue(o);

		} catch (Exception ex) {
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "属性取值错误 "+attrName+" @"+o.getClass()+",Error="+ex.getMessage());
		}
	}


	/**
	 * 调用Beetl，设置某个属性值
	 * @param o
	 * @param value
	 * @param attrName
	 */
	public static void setBeanProperty(Object o, Object value, String attrName) {

		PropertyDescriptorWrap propertyDescriptor = getClassProperty(o.getClass()).get(attrName);
		if(propertyDescriptor==null){
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"属性不存在 "+attrName+" @"+o.getClass());
		}
		try{
			propertyDescriptor.setValue(o,value);
		}catch (Exception  ex){
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "属性赋值错误 "+attrName+" @"+o.getClass()+",Error="+ex.getMessage());
		}


	}

	/**
	 * 调用Beetl，设置某个属性，如果属性类型与值不配置，试图转化.目前用于数据库id转化为pojo的id
	 * @param o
	 * @param value
	 * @param attrName
	 * @see "assgnKeyHolder"
	 */
	public static void setBeanPropertyWithCast(Object o, Object value, String attrName) {
		if (value == null) {
			return;
		}
		Map<String,PropertyDescriptorWrap> map = getClassProperty(o.getClass());
		PropertyDescriptorWrap propertyDescriptor = map.get(attrName);
		Class type = propertyDescriptor.getProp().getPropertyType();
		Object requiredValue = convertValueToRequiredType(value, type);
		propertyDescriptor.setValue(o,requiredValue);

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
				return NumberKit.convertNumberToTargetClass((Number) result, requiredType);
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
	public static <T extends Annotation> T getAnnotation(Class c, Class<T> expect) {
		do {
			T an = (T) c.getAnnotation(expect);
			if (an != null) {
				return an;
			}
			c = c.getSuperclass();
		} while (c != null && c != Object.class);
		return null;

	}


	/**
	 * 得到某个方法的注解，这个注解又包含了特定的注解expect,如下代码，
	 * <pre>@{code
	 *    @MapperExt(DataSourceChange.class)
	 * 	  public @interface DataSource {
	 *			String value()
	 * 	  }
	 *    @DataSource("crm")
	 * 	  public void  query(String userId);
	 *
	 * 	  //查询方法
	 * 	  BeanKit.getAnnotation(method,MapperExt.cass) 返回DataSource注解
	 *
	 *
	 * }</pre>
	 *
	 * @param m
	 * @param expect
	 * @return
	 */
	public static Annotation  getMethodAnnotation(Method m, Class expect) {
		for(Annotation annotation :m.getAnnotations()){
			Annotation target = annotation.annotationType().getAnnotation(expect);
			if(target!=null){
				return annotation;
			}

		}
		return null;

	}

	/**
	 * 得到某个类的注解，这个注解必须包含特定的expectAnnotation注解
	 * @param cls
	 * @param expectAnnotation
	 * @return
	 */
	public static Annotation  getClassAnnotation(Class cls, Class expectAnnotation) {
		for(Annotation annotation :cls.getAnnotations()){
			Annotation target = annotation.annotationType().getAnnotation(expectAnnotation);
			if(target!=null){
				return annotation;
			}

		}
		return null;

	}


	public static <T> T newInstance(Class<T> c) {
		try {

			return c.newInstance();
		} catch (InstantiationException e) {
			throw new BeetlSQLException(BeetlSQLException.OBJECT_INSTANCE_ERROR, e);
		} catch (IllegalAccessException e) {
			throw new BeetlSQLException(BeetlSQLException.OBJECT_INSTANCE_ERROR, e);
		}

	}

	public static Collection newCollectionInstance(Class c) {
		if (c.isInterface()) {
			if (c.isAssignableFrom(List.class)) {
				return new ArrayList();
			} else if (c.isAssignableFrom(Set.class)) {
				return new HashSet();
			} else {
				throw new UnsupportedOperationException("不支持的类 " + c);
			}
		} else {
			return (Collection) newInstance(c);
		}
	}

	/**
	 * 创建一个对象的单例
	 * @param c
	 * @param <T>
	 * @return
	 */
	public static <T> T newSingleInstance(Class<T> c) {
		synchronized (c) {
			Object ins = classInsCache.get(c);
			if (ins != null) {
				return (T) ins;
			}
			ins = newInstance(c);
			classInsCache.put(c, ins);
			return (T) ins;
		}

	}

	/**
	 * 得到属性注解，优先查找getter方法，然后在查找字段
	 * @param c
	 * @param property
	 * @param getter
	 * @param annotationClass
	 * @return
	 * @param <T>
	 */
	public static <T extends Annotation> T getAnnotation(Class c, String property, Method getter,
			Class<T> annotationClass) {
		if (getter == null) {
			throw new NullPointerException("期望POJO类符合javabean规范，" + c + " 没有getter方法");
		}
		T t = getter.getAnnotation(annotationClass);
		if (t != null) {
			return t;
		} else {

			try {
				while (c != null&&c!=Object.class) {
					Map<String,Field> fieldMap = getClassFields(c);
					Field field = fieldMap.get(property);
					if(field!=null){
						t = field.getAnnotation(annotationClass);
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
		Map<String,PropertyDescriptorWrap> map = getClassProperty(c);
		PropertyDescriptorWrap propertyDescriptor = map.get(property);
		if (propertyDescriptor == null) {
			return null;
		}
		return getAnnotation(c, property, propertyDescriptor.getProp().getReadMethod(), annotationClass);

	}

	public static List<Annotation> getAllAnnotation(Class c, String property) {
		Map<String,PropertyDescriptorWrap> map = getClassProperty(c);
		PropertyDescriptorWrap propertyDescriptor = map.get(property);
		if (propertyDescriptor == null) {
			return null;
		}
		Method getter = propertyDescriptor.getProp().getReadMethod();
		Annotation[] array1 = getter.getAnnotations();
		Annotation[] array2 = null;
		Field f = getField(c, property);
		if (f != null) {
			array2 = f.getAnnotations();
		}

		return addAnnotation(array1, array2);

	}

	public static Field getField(Class c, String property) {

		while (c != null&&c!=Object.class) {
			Map<String,Field> fieldMap = getClassFields(c);
			Field field = fieldMap.get(property);
			if(field!=null){
				return field;
			}
			c = c.getSuperclass();
		}
		return null;
	}



	/**
	 * 是否是Java内置类
	 */
	public static boolean isJavaClass(Class entityClass) {
		Package pck = entityClass.getPackage();
		if (pck == null) {
			return false;
		}
		String pkgName = entityClass.getPackage().getName();
		/*todo 低优先级 - 此处后期考虑建立一个java内置包名列表*/
		return pkgName.startsWith("java") || pkgName.startsWith("javax") || Objects
				.isNull(entityClass.getClassLoader());
	}

	public static boolean isBaseDataType(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return true;
		}

		if (clazz.getName().startsWith("java")) {
			return ((clazz == String.class) || clazz == Integer.class || clazz == Byte.class || clazz == Long.class
					|| clazz == Double.class || clazz == Float.class || clazz == Character.class || clazz == Short.class
					|| clazz == BigDecimal.class || clazz == BigInteger.class || clazz == Boolean.class
					|| clazz == java.util.Date.class || clazz == java.sql.Date.class
					|| clazz == java.sql.Timestamp.class || clazz == java.time.LocalDateTime.class
					|| clazz == java.time.LocalDate.class);
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

	public static boolean containViewType(Class[] cls, Class viewType) {
		if (cls.length == 1) {
			return cls[0] == viewType;
		} else if (cls.length == 2) {
			return cls[0] == viewType || cls[1] == viewType;
		}

		for (Class z : cls) {
			if (z == viewType) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 返回集合中的元素类型
	 * @param type
	 * @return
	 */
	public static Class getCollectionType(Type type) {
		if (!(type instanceof ParameterizedType)) {
			return null;
		}
		Class paraType = getParameterTypeClass(type);
		return paraType;
	}

	public static Class[] getMapType(Type type) {
		if (!(type instanceof ParameterizedType)) {
			return null;
		}
		Class[] paraType = getMapParameterTypeClass(type);
		return paraType;
	}

	public static Class getParameterTypeClass(Type t) {
		if (t instanceof WildcardType || t instanceof TypeVariable) {
			// 表示使用了泛型的不确定写法：<?>, <? extends Number>, <T>
			return null;
		} else if (t instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) t).getActualTypeArguments();
			if (types.length == 0) {
				return null;
			}
			Type type = types[0];
			if (type instanceof ParameterizedType) {
				/*集合的元素还是集合*/
				return (Class) ((ParameterizedType) type).getRawType();
			} else if (type instanceof Class) {
				/*集合的元素是确定值*/
				return (Class) types[0];
			} else if (type instanceof TypeVariable) {
				//未定义，则返回null。则通过mapper接口，而不是方法来判断范型类型
				return null;
			} else {
				throw new UnsupportedOperationException(type.toString());
			}

		} else {
			throw new UnsupportedOperationException(t.toString());
		}

	}

	/**
	 * 获取Map的泛型参数
	 *
	 * @param t t
	 * @return {@link Class}[] 位置0 是左边泛型，位置1 是右边泛型
	 */
	public static Class[] getMapParameterTypeClass(Type t) {
		if (t instanceof WildcardType || t instanceof TypeVariable) {
			// 丢失类型
			return null;
		} else if (t instanceof ParameterizedType) {
			Type[] types = ((ParameterizedType) t).getActualTypeArguments();
			if (types.length == 0) {
				return null;
			}
			Class[] classTypes = new Class[2];
			Type type = types[0];
			if (type instanceof ParameterizedType) {
				classTypes[0] = (Class) ((ParameterizedType) type).getRawType();
			} else if (type instanceof Class) {
				classTypes[0] = (Class) types[0];
			} else {
				throw new UnsupportedOperationException(type.toString());
			}

			Type type1 = types[1];
			if (type1 instanceof ParameterizedType) {
				classTypes[1] = (Class) ((ParameterizedType) type1).getRawType();
			} else if (type1 instanceof Class) {
				classTypes[1] = (Class) types[1];
			} else {
				throw new UnsupportedOperationException(type.toString());
			}

			return classTypes;

		} else {
			throw new UnsupportedOperationException(t.toString());
		}

	}


	/**
	 * 简单说获取继承BaseMapper接口时，在泛型中写的实体类
	 *
	 * @param mapperInterface 用户实现的BaseMapper接口
	 * @return {@link Class}
	 */
	public static Class getMapperEntity(Class mapperInterface) {
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





}
