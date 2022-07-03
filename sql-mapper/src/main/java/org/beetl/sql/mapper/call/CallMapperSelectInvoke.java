package org.beetl.sql.mapper.call;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.call.CallReady;
import org.beetl.sql.core.call.InArg;
import org.beetl.sql.core.call.OutArg;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;
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
	Map<Integer,Integer> inMap;
	OutBeanConfig outBeanConfig;
	public CallMapperSelectInvoke(String sql,Class target, Map<Integer,Integer> inMap,OutBeanConfig outBeanConfig){
		this.sql = sql;
		this.target = target;
		this.outBeanConfig = outBeanConfig;
	}

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		CallReady callReady = new CallReady(sql);
		Object outBean = null;
		if(args!=null){
			for(int i=1;i<=args.length;i++){
				Object arg = args[i-1];
				if(inMap.containsKey(i)){
					Integer jdbcType = inMap.get(i);
					if(jdbcType!=null){
						callReady.add(new InArg(arg,jdbcType));
					}else{
						callReady.add(new InArg(arg));
					}

					continue;
				}else if(outBeanConfig!=null&&outBeanConfig.getParamIndex()==i){
					outBean = arg;
					for(Map.Entry<Integer,Class> entry:outBeanConfig.getIndexTypeMap().entrySet()){
						callReady.add(new OutArg(entry.getValue(),entry.getKey()));
					}

				}else{
					//不可能到这
					throw new IllegalStateException(m.toString());

				}
			}
		}

		Object ret = sm.executeCall(callReady,entityClass);
		return ret;
	}
}
