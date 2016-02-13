package org.beetl.sql.core.mapper;

import java.awt.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
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
	
	static Map<Method,MethodDesc> cache = new HashMap<Method,MethodDesc>();
	public static MethodDesc getMetodDesc(SQLManager sm,Method m){
		MethodDesc desc = cache.get(m);
		if(desc!=null) return desc ;
		desc = new MethodDesc();
		desc.parse(sm,m);
		cache.put(m, desc);
		return desc;
		
	}
	
	protected  void parse(SQLManager sm,Method m){
		Class c = m.getDeclaringClass();
		String name = m.getName();
		String sqlId = c.getSimpleName()+"."+name;
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
				throw new BeetlSQLException(BeetlSQLException.UNKNOW_SQL_TYPE,sqlId);
			}
			
		}
		
		Annotation[][] parameterAnnotations = m.getParameterAnnotations();
		Class[] paraTypes = m.getParameterTypes();
		for (int argIndex = 0; argIndex < parameterAnnotations.length; argIndex++) {
			int length = parameterAnnotations[argIndex].length;
			
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
				}
			}
			
			
			
			if(type==0){
				if(KeyHolder.class.isAssignableFrom(paraTypes[argIndex])){
					type =1 ;
					keyHolderPos = argIndex;
				}
			}
		}
		
		
		
		if(this.parasPos.size()==0){
			// try to find root
			for(int i=0;i<paraTypes.length;i++){
				Class cls  = paraTypes[i];
				if(KeyHolder.class.isAssignableFrom(cls)){
					continue ;
				}
				Package pkg = cls.getPackage();
				if(pkg==null) continue ;
				String pkgName = pkg.getName();
				if(pkgName.startsWith("java")){
					continue ;
				}
				this.parasPos.put("_root", i);
				break;
			}
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
