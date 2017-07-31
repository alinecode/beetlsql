package org.beetl.sql.core.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.RowSize;
import org.beetl.sql.core.annotatoin.RowStart;
import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.annotatoin.SqlStatementType;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.para.InsertParamter;
import org.beetl.sql.core.mapper.para.MapperParameter;
import org.beetl.sql.core.mapper.para.PageQueryParamter;
import org.beetl.sql.core.mapper.para.SelectQueryParamter;
import org.beetl.sql.core.mapper.para.UpdateParamter;

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
	public Class entityType = Void.class;
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
		Class retType = m.getReturnType();
		//假设默认类型就是Mapper的泛型类型
		this.entityType = entityClass;
		this.method = m;
		
		
		Sql sql =  (Sql) m.getAnnotation(Sql.class);
		SqlStatementType sqlType = SqlStatementType.AUTO;
		if(sql!=null){
			this.sqlReady = sql.value();	
			sqlType = sql.type();
			
			
		}else{
			SqlStatement st = (SqlStatement) m.getAnnotation(SqlStatement.class);			
			if(st!=null){
				 sqlType = st.type();
				 paramsDeclare = st.params();
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
	
	protected void parseInert(Class[] paras,Class retType){
		if(retType==KeyHolder.class){
			this.type = SM_INSERT_KEYHOLDER;
		}else{
			this.type = SM_INSERT;
		}
		this.parameter = new InsertParamter(method,this.paramsDeclare);
	}
	
	protected void parseUpdate(Class[] paras,Class retType){
		this.type = SM_UPDATE;
		if(paras.length==1){
			Class first = paras[0] ;
			if(List.class.isAssignableFrom(first)){
				this.type = SM_BATCH_UPDATE;
			}else if(first.isArray()){
				Class ct= first.getComponentType();
				if(Map.class.isAssignableFrom(ct)){
					this.type = SM_BATCH_UPDATE;
				}
			}

		}
		
		this.parameter = new UpdateParamter(method,this.paramsDeclare);
	}
	
	protected void parseSelectList(Class[] paras,Class retType){
		Class pageType  =  hasPageQuery(paras,retType);
		boolean isJdbc = this.sqlReady.length()!=0;
		if(pageType!=null){
			Class type =   getType(pageType);
			if(type!=null){
				this.entityType = type;
			}
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
		
		if(List.class.isAssignableFrom(retType)){
			Class type =   getType(retType);
			if(type!=null){
				this.entityType = type;
			}
			this.type = SM_SELECT_LIST;
			parameter =new SelectQueryParamter(method,paramsDeclare,isJdbc);
			return ;
		}
		
		//更改类型为Single
		this.type = SM_SELECT_SINGLE;
		parameter =new SelectQueryParamter(method,paramsDeclare,isJdbc);
		
		
	}
	protected Class getType(Type type){
		if(type instanceof ParameterizedType ){
			return (Class) ((ParameterizedType)type)
					.getActualTypeArguments()[0];
		}else{
			
			return null;
		}
	}
	protected Class hasPageQuery(Class[] paras,Class retType){
		
		if(retType==PageQuery.class){
			return retType;
		}
		
		if(paras.length>=1&&paras[0]==PageQuery.class){
			return paras[0];
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

	private String getTypeDesc(int type) {
		switch (type) {
		case 0:
		case 1:
			return "insert";
		case 2:
		case 3:
			return "select";
		case 4:
		case 5:
			return "update/delete";
		default: {
			throw new IllegalArgumentException("unknow type:" + type);
		}
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
