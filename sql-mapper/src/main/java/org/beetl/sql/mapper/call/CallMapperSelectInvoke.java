package org.beetl.sql.mapper.call;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.call.CallArg;
import org.beetl.sql.core.call.CallReady;
import org.beetl.sql.core.call.InArg;
import org.beetl.sql.core.call.OutArg;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Mapper中存储过程执行
 * <pre>@{code
 * @Call("...")
 * List<User> queryUser(String id,String name);
 *
 * }</pre>
 */
public class CallMapperSelectInvoke extends MapperInvoke {
	String sql ;
	Class target;
	InConfig inConfig;
	OutBeanConfig outBeanConfig;
	boolean isUpdate = false;
	/**
	 * 如果是查询，是返回List还是单个值
	 */
	boolean isSingle = false;

	/**
	 *
	 * @param sql  调用存储过程的sql语句，如 call xx(?,?)
	 * @param target 如果是存储过程返回一个结果集，则可以映射到target的对象上
	 * @param inConfig
	 * @param outBeanConfig 如果存储过程有输出，则此包含了输出的配置
	 * @param isUpdate 存储过程是更新还是查询
	 */
	public CallMapperSelectInvoke(String sql,Class target,InConfig inConfig ,OutBeanConfig outBeanConfig,boolean isUpdate,boolean isSingle){
		this.sql = sql;
		this.target = target;
		this.inConfig = inConfig;
		this.outBeanConfig = outBeanConfig;
		this.isUpdate = isUpdate;
		this.isSingle = isSingle;
	}

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		CallReady callReady = new CallReady(sql);
		Object outBean = null;
		if(args!=null){

			for(int i=0;i<args.length;i++){ // 从1开始，通jdbc 定义
				Object arg = args[i];
				if(inConfig.getArgPositionMap().containsKey(i)){
					int callPosition = inConfig.getArgPositionMap().get(i);
					Integer jdbcType = inConfig.getArgJdbcTypeMap().get(i);
					if(jdbcType!=null){
						callReady.add(callPosition,new InArg(arg,jdbcType));
					}else{
						callReady.add(callPosition,new InArg(arg));
					}

				}else if(outBeanConfig!=null&&outBeanConfig.getParamIndex()==i){
					outBean = arg;
					for(Map.Entry<Integer,Class> entry:outBeanConfig.getIndexTypeMap().entrySet()){
						int callPosition = entry.getKey();
						Class targetType = entry.getValue();
						Integer jdbcType = outBeanConfig.getIndexJdbcMap().get(callPosition);
						if(jdbcType!=null){
							callReady.add(callPosition,new OutArg(targetType,jdbcType));
						}else{
							callReady.add(callPosition,new OutArg(targetType));
						}
					}

				}else{
					//不可能到这
					throw new IllegalStateException(m.toString());

				}
			}
		}

		//存储过程调用
		Object ret = null;
		if(isUpdate){
			ret = sm.executeCall(callReady);
		}else{
			List list  = (List)sm.executeCall(callReady,entityClass);
			if(isSingle){
				return list.size()==0?null:list.get(0);
			}else{
				return list;
			}
		}

		//如果有输出结果
		if(outBean!=null){
			for(CallArg callArg:callReady.getArgs()){
				if(callArg instanceof  OutArg){
					OutArg outArg = (OutArg) callArg;
					Object outValue = outArg.getOutValue();
					int index = outArg.getIndex();
					String attrName = outBeanConfig.indexMap.get(index);
					BeanKit.setBeanProperty(outBean,outValue,attrName);

				}
			}
		}

		return ret;
	}
}
