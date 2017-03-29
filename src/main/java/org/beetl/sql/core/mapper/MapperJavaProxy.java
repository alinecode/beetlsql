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
 * <a href="http://git.oschina.net/xiandafu/beetlsql/issues/54"># 54</a>
 * 封装sqlmanager
 * </p>
 * 
 * @author zhoupan,xiandafu
 */
public class MapperJavaProxy implements InvocationHandler {

	/** The sql manager. */
	protected SQLManager sqlManager;

	/** The entity class. */
	protected Class<?> entityClass;

	

	protected DefaultMapperBuilder builder;
	/**
	 * The Constructor.
	 */
	public MapperJavaProxy() {

	}

	/**
	 * 
	 * @param builder
	 * @param sqlManager
	 * @param mapperInterface
	 */
	public MapperJavaProxy(DefaultMapperBuilder builder,SQLManager sqlManager, Class<?> mapperInterface) {
		super();
		this.sqlManager = sqlManager;
		this.builder = builder;
		this.mapperInterface(mapperInterface);
	}

	

	/**
	 * Mapper interface.
	 *
	 * @param mapperInterface
	 *            the dao2 interface
	 * @return the dao2 proxy
	 */
	public MapperJavaProxy mapperInterface(Class<?> mapperInterface) {
		this.onResolveEntityClassFromMapperInterface(mapperInterface);
		return this;
	}

	

	/**
	 * Entity class.
	 *
	 * @param entityClass
	 *            the entity class
	 * @return the dao2 proxy
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
	 * @return the dao2 proxy
	 */
	public MapperJavaProxy build() {
		this.checkArgs();
		return this;
	}

	/**
	 * 获取BaseMapper&lt;EntityClass&gt;接口的泛型实体参数类.
	 *
	 * @param mapperInterface
	 *            the dao2 interface
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
		String sqlId = this.builder.getIdGen().getId(entityClass, method);
		MapperInvoke invoke = null;
		Class c = method.getDeclaringClass();
		if(c==BaseMapper.class){
			invoke = new InnerMapperInvoke();
			Object ret = invoke.call(this.sqlManager, this.entityClass, sqlId, method, args);
			return ret;
		}else{
			MethodDesc desc = MethodDesc.getMetodDesc(sqlManager, this.entityClass,method, sqlId);
			if(desc.sqlReady.length()==0){
				switch(desc.type){
				case 0 :invoke = new InsertMapperInvoke();break;
				case 1:invoke = new InsertMapperInvoke();break;
				case 2:invoke = new SelecSingleMapperInvoke();break;
				case 3:invoke = new SelectMapperInvoke();break;
				case 4:invoke = new UpdateMapperInvoke();break;
				case 5:invoke = new UpdateBatchMapperInvoke();break;
				case 6:invoke = new PageQueryMapperInvoke();break;
				}
				//handle Void.class ?
				Object ret = invoke.call(this.sqlManager, this.entityClass, sqlId, method, args);
				return ret;
			}else{
				invoke = new SQLReadyExecuteMapperInvoke(desc.type);
				Object ret = invoke.call(this.sqlManager, this.entityClass, desc.sqlReady, method, args);
				return ret;
			}
			
		}
	
		
	}

	

}
