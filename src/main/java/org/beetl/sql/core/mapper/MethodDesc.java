package org.beetl.sql.core.mapper;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.annotatoin.SqlStatementType;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.mapper.para.*;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * dao2 参数
 * 
 * @author xiandafu
 *
 */
public class MethodDesc {
	
	/*对应到SQLManager 操作类型*/
	public final static int SM_INSERT = 0;
	public final static int SM_INSERT_KEYHOLDER = 1;
	public final static int SM_SELECT_SINGLE = 2;
	public final static int SM_SELECT_LIST = 3;
	public final static int SM_UPDATE = 4;
	public final static int SM_BATCH_UPDATE = 5;
	public final static int SM_PAGE_QUERY = 6;
	public final static int SM_SQL_READY_PAGE_QUERY = 7;
	
	public int type = SM_INSERT;
	

	public String sqlReady = "";
	//sqlmanager实际使用的的参数，非方法返回参数，而是泛型
	public Class resultType = Void.class;
	//method 调用参数转为实际参数
	public MapperParameter parameter = null;
	//注解申明的参数名字
	public String paramsDeclare = null;
	
	private Method method = null;

	static Map<CallKey, MethodDesc> cache = new HashMap<CallKey, MethodDesc>();
	
	
	public static MethodDesc getMetodDesc(SQLManager sm, Class entityClass, Method m, String sqlId) {
		CallKey callKey = new CallKey(m,entityClass);
		MethodDesc desc = cache.get(callKey);
		if (desc != null)
			return desc;
		desc = sm.getMapperConfig().createMethodDesc();
        desc.doParse(sm, entityClass, m, sqlId);
		cache.put(callKey, desc);
		return desc;
	}
	
	
	
	protected void doParse(SQLManager sm, Class entityClass, Method m, String sqlId){
		Class[] paras = m.getParameterTypes();
		Type retType = m.getGenericReturnType();
		//假设默认类型就是Mapper的泛型类型
		this.resultType = entityClass;
		this.method = m;
		
		
		Sql sql =  (Sql) m.getAnnotation(Sql.class);
		SqlStatementType sqlType = SqlStatementType.AUTO;
		if(sql!=null){
			this.sqlReady = sql.value();	
			sqlType = sql.type();
			if(sql.returnType()!=Void.class){
				this.resultType = sql.returnType();
			}
			
			
		}else{
			SqlStatement st = (SqlStatement) m.getAnnotation(SqlStatement.class);			
			if(st!=null){
				 sqlType = st.type();
				 paramsDeclare = st.params();
				 if(st.returnType()!=Void.class){
						this.resultType = st.returnType();
				}
			}
			
		}
		//先判断调用sqlmanager类型。
		int inferType=0;
		if (sqlType == SqlStatementType.AUTO) {
			if(sql!=null){
				inferType = getTypeBySql(sqlReady);
			}else{
				inferType = getTypeBySqlId(sm, sqlId);
			}
			
			if(inferType==-1){
				throw new BeetlSQLException(BeetlSQLException.UNKNOW_MAPPER_SQL_TYPE, sqlId+" 请指定Sql类型");
			}				
		
		}else{
			if(sqlType==SqlStatementType.SELECT){
				inferType = SM_SELECT_LIST;
			}else if(sqlType==SqlStatementType.INSERT){
				inferType=SM_INSERT;
			}else{
				inferType = SM_UPDATE;
			}
		}
		//初步判断类型，SM_UPDATE，SM_INSERT,SM_SELECT_LIST
		this.type = inferType;
		//进一步判断具体SQLManager 方法
		switch(type){
			case SM_SELECT_LIST :
				parseSelectList(paras,retType);
				break;
				
			case SM_INSERT:parseInert(paras,retType);break;
			case SM_UPDATE:parseUpdate(paras,retType);break;
		}
		
			
	}
	
	protected void parseInert(Class[] paras,Type retType){
		if(retType==KeyHolder.class){
			this.type = SM_INSERT_KEYHOLDER;
		}else{
			this.type = SM_INSERT;
		}
		this.parameter = new InsertParamter(method,this.paramsDeclare);
	}
	/**
	 * 根据返回参数int 或者int[] 判断是否是批处理。如果都没有，根据第一参数判断
	 * @param paras
	 * @param retType
	 */
	protected void parseUpdate(Class[] paras,Type retType){
		this.parameter = new UpdateParamter(method,this.paramsDeclare);
		this.type = SM_UPDATE;
		Class ret = this.method.getReturnType();
		if(isInt(ret)){
			return ;
		}
		else if(ret.isArray()){
			//如果更新语句返回了int[],
			Class type = ret.getComponentType();
			if(type==int.class||type==long.class||type==short.class||type==Integer.class||type==Short.class||type==Long.class){
				this.type = SM_BATCH_UPDATE;
			}
			return ;
		}
		//通过输入参数判断
		if(paras.length==1){
			Class first = paras[0] ;
			if(List.class.isAssignableFrom(first)&&this.isUpdateBatchByFirstList()){
				this.type = SM_BATCH_UPDATE;
			}else if(first.isArray()){
				Class ct= first.getComponentType();
				if(this.isPojo(ct)){
					this.type = SM_BATCH_UPDATE;
				}
			}
		}
		
	}
	
	private boolean isUpdateBatchByFirstList(){
		
		Type firstType = this.method.getGenericParameterTypes()[0];
		Class type = this.getType(firstType);
		if(type==null){
			//不知道List里面是什么，认为是batchUpdate,兼容以前情况
			return true;
		}
		return isPojo(type);
		
	}
	
	private boolean isPojo(Class type){
		if(type.isPrimitive()){
			return false;
		}
		if(Map.class.isAssignableFrom(type)){
			return true;
		}
		
		String pkg = BeanKit.getPackageName(type);
		if(pkg.startsWith("java.")||pkg.startsWith("javax.")){
			return false;
		}else{
			return true ;
		}
	}
	
	private boolean isInt(Class type){
		if(type==int.class||type==long.class||type==short.class||type==Integer.class||type==Short.class||type==Long.class){
			return true;
		}
		return false;
		
			
	}
	
	protected void parseSelectList(Class[] paras,Type retType){
		Type pageType  =  hasPageQuery(this.method.getGenericParameterTypes(),this.method.getGenericReturnType());
		boolean isJdbc = this.sqlReady.length()!=0;
		if(pageType!=null){
			this.resultType = getPageType(pageType,this.resultType);
			//else否则就默认为mapper类型
			if(isJdbc){
				this.type = SM_SQL_READY_PAGE_QUERY;
				parameter =new PageQueryParamter(method,this.paramsDeclare,isJdbc);
			}else{
				this.type = SM_PAGE_QUERY;
				parameter =new PageQueryParamter(method,paramsDeclare,isJdbc);
			}
			return ;
		}
		parameter =new SelectQueryParamter(method,paramsDeclare,isJdbc);
		if((retType instanceof Class && Map.class.isAssignableFrom((Class)retType))
				||(retType instanceof ParameterizedType&&Map.class.isAssignableFrom(getParamterTypeClass(retType)  ))){
			//如果定义返回结果为Map，无论是否泛型，都认为返回一个Map
			this.type = SM_SELECT_SINGLE;
			this.resultType=Map.class;
			
			return;
		}else if(retType instanceof Class && List.class.isAssignableFrom((Class)retType)){
			this.type = SM_SELECT_LIST;
			return ;
		}
		else if(List.class.isAssignableFrom(getParamterTypeClass(retType))){
			Class type =   getType(retType);
			if(type!=null){
				this.resultType = type;
			}
			this.type = SM_SELECT_LIST;
			return ;
		}
		
		this.resultType = method.getReturnType();
		//更改类型为Single
		this.type = SM_SELECT_SINGLE;
		
		
	}
	
	protected Class getPageType(Type type,Class defaultClass){
		if(type instanceof Class){
			return defaultClass;
		}else {
			Type t = ((ParameterizedType)type)
					.getActualTypeArguments()[0];
			if(t instanceof ParameterizedType){
				return getParamterTypeClass(t);
			}else if(t instanceof WildcardType|| t instanceof TypeVariable ) {
				//丢失类型，只能用返回类型来判断
				return defaultClass;
			}
			else{
				return   (Class)t;
			}
		}
	}
	protected Class getType(Type type){
		if(type instanceof Class){
			return (Class)type;
		}else
		if(type instanceof ParameterizedType ){
			Type t = ((ParameterizedType)type)
					.getActualTypeArguments()[0];
			if(t instanceof ParameterizedType){
				return getParamterTypeClass(t);
			}else{
				return (Class)t;
			}
		}else{
			
			return null;
		}
	}
	
	protected Class getParamterTypeClass(Type t){
		if(t instanceof Class){
			return (Class)t;
		}else{
			return (Class)((ParameterizedType)t).getRawType();
		}
		
	}
	
	protected Type hasPageQuery(Type[] paras,Type retType){
		if(getParamterTypeClass(retType)==PageQuery.class){
			return retType;
		}
		
		
		if(paras.length>=1&&getParamterTypeClass(paras[0])==PageQuery.class){
			return  paras[0];
		}
		
		return null;
	}
	
	

	/**
	 * 根据sql语句判断sql类型，用于对应到SQLManager操作
	 * @param sql
	 * @return
	 */
	private int getTypeBySql(String sql) {
		
		String sqlType = getFirstToken(sql);
		
		if (sqlType.equals("select")) {
			return SM_SELECT_LIST;
		} else if (sqlType.equals("insert")) {
			return SM_INSERT;
		} else if (sqlType.equals("delete")) {
			return SM_UPDATE;
		} else if (sqlType.equals("update")) {
			return SM_UPDATE;
		} else if(sqlType.equals("create")){
			return SM_UPDATE;
		}else if(sqlType.equals("drop")){
			return SM_UPDATE;
		}else if(sqlType.equals("truncate")){
    	    return SM_UPDATE;
    	}
		else {
			return -1; //unknow
		}
	}
	
	private static String getFirstToken(String sql){
		boolean start = false;
		int startIndex = 0;
		for(int i=0;i<sql.length();i++){
			char c = sql.charAt(i);
			if(!start){
				if(!isSpecialChar(c)){
					start = true;
					startIndex = i;
					
				}
				continue;
			}
			
			if(isSpecialChar(c)){
				return sql.substring(startIndex,i).toLowerCase();
			}
			
		}
		return "";
	}
	
	private static boolean isSpecialChar(char c){
		return c==' '||c=='\t'||c=='\r'||c=='\n';
	}
	
	
	protected int getTypeBySqlId(SQLManager sm, String sqlId) {
		String sql = null;
		SQLScript script = sm.getScript(sqlId);
		sql = script.getSql();
		int ret = getTypeBySql(sql);
		if(ret==-1){
			throw new BeetlSQLException(BeetlSQLException.UNKNOW_MAPPER_SQL_TYPE, sqlId+" 请指定Sql类型");
		}else{
			return ret;
		}
		
		
	}

	

	
	protected Class getRetType(Method method,Class entityClass){
		Type type = method.getGenericReturnType();
		if(type instanceof ParameterizedType ){
			return (Class) ((ParameterizedType) method.getGenericReturnType())
					.getActualTypeArguments()[0];
		}else{
			return  entityClass;
		}
	}
	static class CallKey{
		Method m;
		Class entityClass;
		public CallKey(Method m,Class entityClass){
			this.m = m;
			this.entityClass = entityClass;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((entityClass == null) ? 0 : entityClass.hashCode());
			result = prime * result + ((m == null) ? 0 : m.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			
			CallKey other = (CallKey) obj;
			if(other.entityClass==this.entityClass&&this.m.equals(other.m)){
				return true ;
			}else{
				return false;
			}
			
			
		}
		
	}

}
