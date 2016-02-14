package org.beetl.sql.core.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.RowSize;
import org.beetl.sql.core.annotatoin.RowStart;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.annotatoin.SqlStatementType;
import org.beetl.sql.core.db.KeyHolder;


/**
 * mapper 参数
 * @author xiandafu
 *
 */
public class MethodDesc {
	public Map<String,Integer> parasPos = new HashMap<String,Integer>();
	//0 insert , 1 insert with key holder, 2 select single ,3 select list 4 update 5 batchUpdate
	public int type = 0;
	public Method method = null;
	//如果存在翻页，pagger［0］ ＝offet,pagger[1]= size;
	public int[] paggerPos = null;
	// -1 表示返回一个KeyHolder，否则，使用指定位置的参数
	public int keyHolderPos = -1;
	public int mapPos = -1;
	
	static Map<Method,MethodDesc> cache = new HashMap<Method,MethodDesc>();
	public static MethodDesc getMetodDesc(SQLManager sm,Class entityClass,Method m){
		MethodDesc desc = cache.get(m);
		if(desc!=null) return desc ;
		desc = new MethodDesc();
		desc.parse(sm,entityClass,m);
		cache.put(m, desc);
		return desc;
		
	}
	
	protected  void parse(SQLManager sm,Class entityClass,Method m){
		
		String name = m.getName();
		String sqlId = entityClass.getSimpleName()+"."+name;
		SqlStatement st = (SqlStatement)m.getAnnotation(SqlStatement.class);
		// 先初步判断 sql 类型
		type = 0;
		if(st!=null){
			SqlStatementType sqlType = st.type();
			if(sqlType==SqlStatementType.INSERT){
				type = 0;
			}else if(sqlType==SqlStatementType.SELECT){
				type = 2;
			}else{
				type = 4;
			}
		}else{
			SQLScript script = sm.getScript(sqlId);
			String sql = script.getSql().trim();
			if(sql.startsWith("select")){
				type = 2;
			}else if(sql.startsWith("insert")){
				type = 0;
			}else if(sql.startsWith("delete")){
				type = 4;
			}else if(sql.startsWith("update")){
				type = 4;
			}else{
				throw new BeetlSQLException(BeetlSQLException.UNKNOW_MAPPER_SQL_TYPE,sqlId);
			}
			
		}
		//纪录错误位置
		List<Integer> errorPara = new LinkedList<Integer>();
		Annotation[][] parameterAnnotations = m.getParameterAnnotations();
		Class[] paraTypes = m.getParameterTypes();
		for (int argIndex = 0; argIndex < parameterAnnotations.length; argIndex++) {
			int length = parameterAnnotations[argIndex].length;
			if(length==0){
				Class cls  = paraTypes[argIndex];
				if(KeyHolder.class.isAssignableFrom(cls)){
					if(type==0){
						type =1 ;
						keyHolderPos = argIndex;
						
					}else{
						errorPara.add(argIndex);
					}
					continue ;
				}
				
				if(Map.class.isAssignableFrom(cls)){
					if(!this.parasPos.containsKey("_root")){
						mapPos = argIndex;
						
					}else{
						errorPara.add(argIndex);
					}
					continue ;
					
				}
				
				Package pkg = cls.getPackage();
				if(pkg==null){
					errorPara.add(argIndex);
					continue ;
				}
				
				String pkgName = pkg.getName();
				if(pkgName.startsWith("java")){
					errorPara.add(argIndex);
					continue ;
				}
				
				if(mapPos!=-1){
					//已经有map参数了，不能与pojo并存
					errorPara.add(argIndex);
				}else{
					//pojo
					if(this.parasPos.containsKey("_root")){
						int pos = this.parasPos.get("_root");
						Class rootType = paraTypes[pos];
						throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER,sqlId+"接口参数定义错误，无法映射,在第"+pos+"个参数已经定义了_root:"+rootType);
					}else{
						this.parasPos.put("_root", argIndex);
						
					}
				}
				
				
				
			}else{
				for (int annIndex = 0; annIndex < length; annIndex++) {
					Annotation paramAnn = parameterAnnotations[argIndex][annIndex];
					// Param注解.
					if (paramAnn instanceof Param) {
						Param param = (Param) paramAnn;
						parasPos.put(param.value(),argIndex);
						
					}else if(paramAnn instanceof RowStart){
						if(paggerPos==null){
							paggerPos = new int[2];
						}
						paggerPos[0] =argIndex;
					}else if(paramAnn instanceof RowSize){
						if(paggerPos==null){
							paggerPos = new int[2];
						}
						paggerPos[1] =argIndex;
					}else{
						errorPara.add(argIndex);
						}
				}
			}
			
			
			
			
			
		}
		
		
		if(errorPara.size()!=0){
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER,sqlId+"接口参数在"+errorPara+"定义错误，无法映射");
			
		}
		
		Class returnType = m.getReturnType();
		if(type==0){
			if(KeyHolder.class.isAssignableFrom(returnType)){
				type =1 ;
				keyHolderPos = -1;
			}
			return ;
		}else if(type==2){
			if(List.class.isAssignableFrom(returnType)){
				type=3;
			}
		}
		
		
		
	}
	
}
