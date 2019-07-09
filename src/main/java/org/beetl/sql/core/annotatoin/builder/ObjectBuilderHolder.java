package org.beetl.sql.core.annotatoin.builder;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.annotatoin.Builder;
import org.beetl.sql.core.kit.BeanKit;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectBuilderHolder {
	static Map<Class, Object> classHandlerMap = new ConcurrentHashMap<Class, Object>();
	Annotation beanAnnotaton;
	Builder builderAnotation;
	Object instance;

	public ObjectBuilderHolder(Annotation beanAnnotaton, Builder builderAnotation) {
		this.beanAnnotaton = beanAnnotaton;
		this.builderAnotation = builderAnotation;
		this.instance = newInstance(builderAnotation.value());
	}

	public Object newInstance(Class objectHandlerClz) {
		if (classHandlerMap.containsKey(objectHandlerClz)) {
			return classHandlerMap.get(objectHandlerClz);
		}

		Object objectHanlder = BeanKit.newInstance(objectHandlerClz);
		if (objectHanlder instanceof ObjectPersistBuilder || objectHanlder instanceof ObjectSelectBuilder) {
			classHandlerMap.put(objectHandlerClz, objectHanlder);
			return objectHanlder;
		} else {
			throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,
					objectHandlerClz + " 需要实现ObjectPersistBuilder或者ObjectSelectBuilder接口");
		}

	}

	public Annotation getBeanAnnotaton() {
		return beanAnnotaton;
	}

	public void setBeanAnnotaton(Annotation beanAnnotaton) {
		this.beanAnnotaton = beanAnnotaton;
	}

	public Builder getBuilderAnotation() {
		return builderAnotation;
	}

	public void setBuilderAnotation(Builder builderAnotation) {
		this.builderAnotation = builderAnotation;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}
}
