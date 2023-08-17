package com.beetl.sql.pref;

import org.beetl.sql.clazz.kit.BeetlSQLException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class BeanPropertyWriteFactory {


	private final static Map<ClassLoader, ByteClassLoader> classLoaders = new ConcurrentHashMap<>();
	static Map<Class, BeanPropertyAsm> propertyWriteMap = new ConcurrentHashMap<>();

	public static BeanPropertyAsm getBeanProperty(Class c){
		BeanPropertyAsm propertyWrite = propertyWriteMap.get(c);
		if(propertyWrite!=null){
			return propertyWrite;
		}

		propertyWrite  = propertyWriteMap.computeIfAbsent(c, new Function() {
			@Override
			public Object apply(Object o) {
				try{
					ClassLoader beanClassLoader = c.getClassLoader();
					byte[] bs = BeanAsmCode.genCode(c);
					String name = BeanAsmCode.getWriteClassName(c);
					ByteClassLoader byteClassLoader = classLoaders.get(beanClassLoader);
					if(byteClassLoader==null){
						byteClassLoader = new ByteClassLoader(beanClassLoader);
						classLoaders.put(beanClassLoader,byteClassLoader);
					}
					Class<?> enhanceClass = byteClassLoader.findClassByName(name);
					if (enhanceClass == null) {
						enhanceClass = byteClassLoader.defineClass(name, bs);
					}
					BeanPropertyAsm beanPropertyAsm = (BeanPropertyAsm)enhanceClass.newInstance();
					BeanPropertyAsmWrapper writeWrapper = new BeanPropertyAsmWrapper(beanPropertyAsm);
					return writeWrapper;

				}catch (Exception exception){

					throw new BeetlSQLException(BeetlSQLException.ERROR,"代码生成Bean错误 "+exception.getMessage(),exception);
				}
			}
		});

		return propertyWrite;

	}


	public static class ByteClassLoader extends ClassLoader {

		public ByteClassLoader(ClassLoader parent) {
			super(parent);
		}

		public Class<?> defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}

		public Class<?> findClassByName(String clazzName) {
			try {
				return getParent().loadClass(clazzName);
			} catch (ClassNotFoundException e) {
				// ignore
			}
			return null;
		}
	}

}
