package org.beetl.sql.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MetadataManager;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.QueryMapping;
import org.beetl.sql.core.mapping.RowMapperResultSetExt;
import org.beetl.sql.core.mapping.handler.BeanHandler;
import org.beetl.sql.core.mapping.handler.BeanListHandler;
import org.beetl.sql.core.mapping.handler.MapListHandler;
import org.beetl.sql.core.mapping.handler.ScalarHandler;

public class SQLScript {
	
	SQLManager sm;
	String id ;
    String sql;
    SQLSource sqlSource;
	String jdbcSql;
	
	QueryMapping queryMapping = QueryMapping.getInstance();

	public SQLScript(SQLSource sqlSource,SQLManager sm) {
		this.sqlSource = sqlSource;
		this.sql = sqlSource.getTemplate();
		this.sm = sm ;
		this.id = sqlSource.getId();

	}

	protected SQLResult run(Map<String, Object> paras) {
		GroupTemplate gt = sm.beetl.getGroupTemplate();
		Template t = gt.getTemplate(sqlSource.getId());
		List<Object> jdbcPara = new LinkedList<Object>();
		
		if(paras != null){
			for (Entry<String, Object> entry : paras.entrySet()) {
				t.binding(entry.getKey(), entry.getValue());
			}
		}
		
		t.binding("_paras", jdbcPara);
		t.binding("_manager", this.sm);
		t.binding("_id", id);

		String jdbcSql = t.render();
		SQLResult result = new SQLResult();
		result.jdbcSql = jdbcSql;
		result.jdbcPara = jdbcPara;
		return result;
	}
	
	public int insert(Object paras){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		SQLResult result = this.run(map);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id,sql, true,objs);
		sql = ctx.getSql();
		objs = ctx.getParas();
		PreparedStatement ps = null;
		Connection conn  = null;
		try {
			conn = sm.getDs().getConn(this.id,true,sql,objs);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < objs.size(); i++)
				ps.setObject(i + 1, objs.get(i));
			int ret = ps.executeUpdate();
			this.callInterceptorAsAfter(ctx,ret);
			return ret;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps);
		}
	}
	
	public int insert(Object paras,KeyHolder holder){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		PreparedStatement ps = null;
		Connection conn  = null;
		try {
			
		
			if(this.sqlSource.getIdType()==DBStyle.ID_SEQ){
				String seqName = sqlSource.getSeqName();
				//序列。
				conn = sm.getDs().getMaster();
				PreparedStatement seqPs = conn.prepareStatement("select "+seqName+".NEXTVAL from dual");
				ResultSet seqRs = seqPs.executeQuery();
				
				if(seqRs.next()){
					Object key =seqRs.getObject(0);
					// 也许要做类型转化，todo
					holder.setKey(key);
					map.put("_tempKey", key); //TODO 这里貌似有问题。上面已经this.run(map)了
				}
				seqRs.close();
				seqPs.close();
				
			}
		
	
		SQLResult result = this.run(map);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id,sql, true,objs);
		sql = ctx.getSql();
		objs = ctx.getParas();
		
		if(conn==null){
			conn = sm.getDs().getConn (id,true,sql,objs);
		}
		
		if(this.sqlSource.getIdType()==DBStyle.ID_ASSIGN){
			ps = conn.prepareStatement(sql);
		}else if(this.sqlSource.getIdType()==DBStyle.ID_AUTO){
			ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		}else{
			ps =conn.prepareStatement(sql);
		}
			
			for (int i = 0; i < objs.size(); i++)
				ps.setObject(i + 1, objs.get(i));
			int ret = ps.executeUpdate();
			
			if(this.sqlSource.getIdType()==DBStyle.ID_AUTO){
				ResultSet seqRs = ps.getGeneratedKeys();
				seqRs.next();
				Object key =seqRs.getObject(1);
				holder.setKey(key);
				seqRs.close();
			}
			this.callInterceptorAsAfter(ctx,ret);
			return ret;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps);
		}
	}
	
	/**
	 * 查询是传入Pojo实体，直接绑定到_root。可以在sql中使用#age#，而无需#user.age#
	 * @param paras
	 * @param target
	 * @return
	 */
	public <T> T singleSelect(Object paras, Class<T> target) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		return this.singleSelect(map, target);
	}
	
	
	
	
	public <T> T singleSelect(Map<String, Object> map, Class<T> target) {
		
		List<T> result = select(target, map);
		
		if(result.size() > 0){
			return result.get(0);
		}
		return null;
	}
	
	public <T> List<T> select(Class<T> clazz,Object paras) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		return this.select(clazz, map);
	}
	
	public <T> List<T> select(Class<T> clazz, Map<String, Object> paras, RowMapper<T> mapper) {
		SQLResult result = run(paras);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<T> resultList = null;
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id,sql,false, objs);
		sql = ctx.getSql();
		objs = ctx.getParas();
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id,false,sql,objs);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < objs.size(); i++)
				ps.setObject(i + 1, objs.get(i));
			rs = ps.executeQuery();
			
			if(mapper != null){
				BeanProcessor beanProcessor = new BeanProcessor(this.sm.getNc());
				resultList = new RowMapperResultSetExt<T>(mapper,beanProcessor).handleResultSet(rs,clazz);
				this.callInterceptorAsAfter(ctx,resultList);
				
			}else{
				resultList = mappingSelect(rs, clazz);		
			}
				
			this.callInterceptorAsAfter(ctx,resultList);
			return resultList;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps,rs);
		}
		
	}
	/**
	 * 查询,返回一个pojo集合
	 * 
	 * @param conn
	 * @param paras
	 * @param mapping
	 * @return
	 */
	public <T> List<T> select(Class<T> clazz, Map<String, Object> paras) {
		return this.select(clazz, paras,null);
	}
	
	/**
	 * 
	 * @MethodName: mappingSelect   
	 * @Description: 查询情景不同，调用的handler不同  
	 * @param @param rs
	 * @param @param clazz
	 * @param @param paras
	 * @param @return  
	 * @return List<T>  
	 * @throws
	 */
	public <T> List<T> mappingSelect(ResultSet rs, Class<T> clazz){
		List<T> resultList = new ArrayList<T>();
		
		if(isBaseDataType(clazz)){ //基本数据类型，如果有需要可以继续在isBaseDataType()添加
			T result = queryMapping.query(rs, new ScalarHandler<T>(clazz));
			resultList.add(result);
		} else if(clazz.isAssignableFrom(Map.class)){ //如果是Map的子类或者父类，返回List<Map<String,Object>>
			resultList = (List<T>) queryMapping.query(rs, new MapListHandler(this.sm.getNc()));
		} else{
			resultList = queryMapping.query(rs, new BeanListHandler<T>(clazz, this.sm.getNc()));
		}
		
		return resultList;
		
	}
	
	/**  
	  * 判断一个类是否为基本数据类型。  
	  * @param clazz 要判断的类。  
	  * @return true 表示为基本数据类型。  
	  */ 
	 private static boolean isBaseDataType(Class<?> clazz)
	 {   
	     return 
	     (   
	         clazz.equals(String.class) ||   
	         clazz.equals(Integer.class)||   
	         clazz.equals(Byte.class) ||   
	         clazz.equals(Long.class) ||   
	         clazz.equals(Double.class) ||   
	         clazz.equals(Float.class) ||   
	         clazz.equals(Character.class) ||   
	         clazz.equals(Short.class) ||   
	         clazz.equals(BigDecimal.class) ||   
	         clazz.equals(BigInteger.class) ||   
	         clazz.equals(Boolean.class) ||   
	         clazz.equals(Date.class) ||   
	         clazz.isPrimitive()   
	     );   
	 }
	

	/**
	 *  翻页 
	 * @param paras
	 * @param mapping
	 * @param start
	 * @param end
	 * @return
	 */
	public <T> List<T> select(Map<String, Object> paras,
			Class<T> mapping,RowMapper<T> mapper, long start, long size) {
		SQLScript pageScript = this.sm.getPageSqlScript(this.id);
		if(paras==null) paras = new HashMap<String, Object>();
		this.sm.getDbStyle().initPagePara(paras, start, size);
		return pageScript.select(mapping, paras,mapper);
//		return pageScript.se
	}
	
	/**
	 * 翻页，通上
	 * @param paras
	 * @param mapping
	 * @param start
	 * @param end
	 * @return
	 */
	public <T> List<T> select(Object paras,
			Class<T> mapping, RowMapper<T> mapper,long start, long end) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		return this.select(map, mapping,mapper, start, end);
	}
	/**
	 * 翻页总数
	 * @param paras
	 * @return
	 */
	public long selectCount(Object paras){
		return this.singleSelect(paras, Long.class);
	}
	
	public long selectCount(Map<String, Object> paras){
		return this.singleSelect(paras, Long.class);
	}
	
	
	public int update(Map<String, Object> paras){
		
		SQLResult result = run(paras);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id,sql,true, objs);
		sql = ctx.getSql();
		objs = ctx.getParas();
		int rs = 0;
		PreparedStatement ps = null;
		// 执行jdbc
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id,true,sql,objs);
			ps = conn .prepareStatement(sql);
			for (int i = 0; i < objs.size(); i++)
				ps.setObject(i + 1, objs.get(i));
			rs = ps.executeUpdate();
			this.callInterceptorAsAfter(ctx,rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps);
		}
		return rs;
	}
	
	public int update(Object obj) {
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("_root", obj);
		return this.update(paras);
	}
	
	public int[] updateBatch(Map<String, Object>[] maps) {
		int[] rs = null;
		PreparedStatement ps = null;
		// 执行jdbc
		Connection conn = null;
		InterceptorContext ctx  = null;
		try {
			
			for(int k = 0;k<maps.length;k++ ){
				Map<String, Object> paras = maps[k];
				SQLResult result = run(paras);
				List<Object> objs = result.jdbcPara;
				
				if(ps==null){
					conn = sm.getDs().getConn(id,true,sql,objs);
					ps = conn.prepareStatement(result.jdbcSql);
					ctx = this.callInterceptorAsBefore(this.id,sql, true,Collections.EMPTY_LIST);
				}	
				for (int i = 0; i < objs.size(); i++)
					ps.setObject(i + 1, objs.get(i));
				ps.addBatch();
			
			}
			rs = ps.executeBatch();
			this.callInterceptorAsAfter(ctx,rs);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps);
		}
		return rs;
	}
	/****
	 * 批量更新
	 * @param obj
	 * @return
	 */
	public int[] updateBatch(List<?> list) {

		int[] rs = null;
		PreparedStatement ps = null;
		Connection conn = null;
		// 执行jdbc
		InterceptorContext ctx = null;
		try {
		
			for(int k = 0;k<list.size();k++ ){
				Map<String, Object> paras = new HashMap<String, Object>();
				paras.put("_root", list.get(k));
				SQLResult result = run(paras);
				List<Object> objs = result.jdbcPara;
				
				if(ps==null){
					conn = sm.getDs().getConn(id,true,sql,objs);
					ps = conn.prepareStatement(result.jdbcSql);
					ctx = this.callInterceptorAsBefore(this.id,sql, true,Collections.emptyList());
				}				
				
				for (int i = 0; i < objs.size(); i++)
					ps.setObject(i + 1, objs.get(i));
				ps.addBatch();
				
			}
			rs = ps.executeBatch();
			this.callInterceptorAsAfter(ctx,rs);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps);
		}
		return rs;
	}

	/**
	 * 通过主键查询单条记录
	 * @param obj
	 * @return
	 */
	public <T> T unique(Class<T> clazz,RowMapper<T> mapper, Object objId) {
		
		MetadataManager mm = this.sm.getDbStyle().getMetadataManager();
		String pk= mm.getIds(this.sm.getNc().getTableName(clazz));
	
		Map<String, Object> paras =new HashMap<String,Object>();
		paras.put(pk,objId);
		SQLResult result = run(paras);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		ResultSet rs = null;
		PreparedStatement ps = null;
		T model = null;
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id,sql, false,objs);
		sql = ctx.getSql();
		objs = ctx.getParas();
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id,false,sql,objs);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < objs.size(); i++)
				ps.setObject(i + 1, objs.get(i));
			rs = ps.executeQuery();
			model = queryMapping.query(rs, new BeanHandler<T>(clazz, this.sm.getNc()));
			this.callInterceptorAsAfter(ctx,model);
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps,rs);
		}
		return model;
	}

	/**
	 * 根据Id删除
	 * @param j 
	 * @param user
	 */
	public int deleteById(Class<?> clazz, Object objId ) {
		
		MetadataManager mm = this.sm.getDbStyle().getMetadataManager();
		String pk = mm.getIds(this.sm.getNc().getTableName(clazz));
		
	
		Map<String, Object> paras =new HashMap<String,Object>();
		paras.put(pk,objId);
		
		SQLResult result = run(paras);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id,sql,true, objs);
		sql = ctx.getSql();
		objs = ctx.getParas();
		int rs = 0;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id,true,sql,objs);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < objs.size(); i++)
				ps.setObject(i + 1, objs.get(i));
			rs = ps.executeUpdate();
			this.callInterceptorAsAfter(ctx,rs);
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,e);
		} finally {
			clean(conn,ps);
		}
		return rs;
	}
	
	private void clean(Connection conn,PreparedStatement ps,ResultSet rs){
		try {
			if(rs!=null)rs.close();
			if(ps!=null)ps.close();
			if(!this.sm.getDs().isTransaction()){
				if(conn!=null)conn.close();
			}
		} catch (SQLException e) {
		
		}
	}
	
	private void clean(Connection conn,PreparedStatement ps){
		this.clean(conn, ps,null);
	}
	


	private InterceptorContext callInterceptorAsBefore(String sqlId,String sql,boolean isUpdate,List<Object> paras){
		
		InterceptorContext ctx = new InterceptorContext(sqlId,sql,paras,isUpdate);
		for(Interceptor in:sm.inters){
			in.before(ctx);
		}
		return ctx;
	}
	
	private void callInterceptorAsAfter(InterceptorContext ctx,Object result ){
		if(sm.inters==null) return  ;
		if(!ctx.isUpdate()){
			if(result instanceof List){
				List list = (List)result;
				ctx.setResult(list.size());
			}else{
				ctx.setResult(0);
			}
						
		}else{
			ctx.setResult(result);
		}
		for(Interceptor in:sm.inters){
			in.after(ctx);
		}
		return ;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
