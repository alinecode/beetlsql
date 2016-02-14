package org.beetl.sql.core.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.annotatoin.SqlStatementType;
/**
 * Java代理实现.
 * <p>
 * <a href="http://git.oschina.net/xiandafu/beetlsql/issues/54"># 54
 * 封装sqlmanager
 * </p>
 * 
 * <pre>
 * public interface BaseMapper<T>
{
    insert(T t);
    List<T> template(T t)
}


public interface UserMapper<User> extends BaseMapper{
    public void updateXXX(int id,int status);
}

UserMapper uerDao = sqlManager.getMapper(UserMapper) ;
userDao.insert(new User());
userDao.updateXXX(1,2);

updateXXX
===
update user set status = #status# where id=#id#
 * </pre>
 */
public class MapperJavaProxy implements InvocationHandler {

	/** The sql manager. */
	protected SQLManager sqlManager;

	/** The entity class. */
	protected Class<?> entityClass;

	/** The namespace. */
	protected String namespace;

	/**
	 * The Constructor.
	 */
	public MapperJavaProxy() {

	}

	/**
	 * The Constructor.
	 *
	 * @param sqlManager
	 *            the sql manager
	 * @param mapperInterface
	 *            the entity class or mapper class
	 */
	public MapperJavaProxy(SQLManager sqlManager, Class<?> mapperInterface) {
		super();
		this.sqlManager = sqlManager;
		this.mapperInterface(mapperInterface);
	}

	/**
	 * Sql manager.
	 *
	 * @param sqlManager
	 *            the sql manager
	 * @return the mapper proxy
	 */
	public MapperJavaProxy sqlManager(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
		return this;
	}

	/**
	 * Mapper interface.
	 *
	 * @param mapperInterface
	 *            the mapper interface
	 * @return the mapper proxy
	 */
	public MapperJavaProxy mapperInterface(Class<?> mapperInterface) {
		this.onResolveEntityClassFromMapperInterface(mapperInterface);
		this.onResolveNamespace(mapperInterface);
		return this;
	}

	/**
	 * Namespace.
	 *
	 * @param namespace
	 *            the namespace
	 * @return the mapper proxy
	 */
	public MapperJavaProxy namespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	/**
	 * Entity class.
	 *
	 * @param entityClass
	 *            the entity class
	 * @return the mapper proxy
	 */
	public MapperJavaProxy entityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
		return this;
	}

	/**
	 * Check args.
	 */
	protected void checkArgs() {
			}

	/**
	 * Builds the.
	 *
	 * @return the mapper proxy
	 */
	public MapperJavaProxy build() {
		this.checkArgs();
		return this;
	}

	/**
	 * 获取BaseMapper<EntityClass>接口的泛型实体参数类.
	 *
	 * @param mapperInterface
	 *            the mapper interface
	 */
	protected void onResolveEntityClassFromMapperInterface(Class<?> mapperInterface) {
		if (mapperInterface.isInterface()) {
			Type[] faces = mapperInterface.getGenericInterfaces();
			if (faces.length > 0 && faces[0] instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) faces[0];
				if (pt.getActualTypeArguments().length > 0) {
					this.entityClass = (Class<?>) pt.getActualTypeArguments()[0];
					
				}
			}
		} else {
			throw new IllegalArgumentException ("mapperInterface is not interface.");
		}
	}

	/**
	 * On resolve namespace.
	 *
	 * @param mapperInterface
	 *            the mapper interface
	 */
	protected void onResolveNamespace(Class<?> mapperInterface) {
		this.namespace =  this.entityClass.getSimpleName() ;
	}

	



	/**
	 * Invoke.
	 *
	 * @param proxy
	 *            the proxy
	 * @param method
	 *            the method
	 * @param args
	 *            the args
	 * @return the object
	 * @throws Throwable
	 *             the throwable
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		MapperInvoke invoke = null;
		Class c = method.getDeclaringClass();
		if(c==BaseMapper.class){
			invoke = new InnerMapperInvoke();
		}else{
			MethodDesc desc = MethodDesc.getMetodDesc(sqlManager, this.entityClass,method);
			switch(desc.type){
			case 0 :invoke = new InsertMapperInvoke();break;
			case 1:invoke = new InsertMapperInvoke();break;
			case 2:invoke = new SelecSingleMapperInvoke();break;
			case 3:invoke = new SelectMapperInvoke();break;
			case 4:invoke = new UpdateMapperInvoke();break;
			}
		}
		//handle Void.class ?
		Object ret = invoke.call(this.sqlManager, this.entityClass, namespace, method, args);
		return ret;
		
	}

	

}
