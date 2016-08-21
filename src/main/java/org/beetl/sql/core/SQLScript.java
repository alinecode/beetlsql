package org.beetl.sql.core;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.db.ClassDesc;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MetadataManager;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
import org.beetl.sql.core.kit.EnumKit;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.QueryMapping;
import org.beetl.sql.core.mapping.RowMapperResultSetExt;
import org.beetl.sql.core.mapping.handler.BeanHandler;
import org.beetl.sql.core.mapping.handler.BeanListHandler;
import org.beetl.sql.core.mapping.handler.MapListHandler;
import org.beetl.sql.core.mapping.handler.ScalarHandler;
import org.beetl.sql.core.orm.MappingEntity;

public class SQLScript {

	final SQLManager sm;
	final String id;
	final String sql;
	final SQLSource sqlSource;
	final String dbName ;

	final QueryMapping queryMapping = QueryMapping.getInstance();

	public SQLScript(SQLSource sqlSource, SQLManager sm) {
		this.sqlSource = sqlSource;
		this.sql = sqlSource.getTemplate();
		this.sm = sm;
		this.id = sqlSource.getId();
		this.dbName = sm.getDbStyle().getName();

	}

	protected SQLResult run(Map<String, Object> paras) {
		GroupTemplate gt = sm.beetl.getGroupTemplate();
		Template t = gt.getTemplate(sqlSource.getId());
		List<Object> jdbcPara = new LinkedList<Object>();

		if (paras != null) {
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
		
		result.mapingEntrys= (List<MappingEntity>)t.getCtx().getGlobal("_mapping");
		
		return result;
	}

	protected SQLResult run(Map<String, Object> paras, String parentId) {
		GroupTemplate gt = sm.beetl.getGroupTemplate();
		Template t = gt.getTemplate(sqlSource.getId(), parentId);
		List<Object> jdbcPara = new LinkedList<Object>();

		if (paras != null) {
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
		result.mapingEntrys= (List<MappingEntity>)t.getCtx().getGlobal("_mapping");
		return result;
	}

	public int insert(Object paras) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		addParaIfAssignId(map);
		
		PreparedStatement ps = null;
		Connection conn = null;
	

		SQLResult result = this.run(map);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, true, objs,map);
		sql = ctx.getSql();
		objs = ctx.getParas();

		try {

			conn = sm.getDs().getConn(this.id, true, sql, objs);
			ps = conn.prepareStatement(sql);
			this.setPreparedStatementPara(ps, objs);
			int ret = ps.executeUpdate();
			this.callInterceptorAsAfter(ctx, ret);
			return ret;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
		}
	}

	public int insert(Object paras, KeyHolder holder) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		addParaIfAssignId(map);
		
		PreparedStatement ps = null;
		Connection conn = null;
		try {



			SQLResult result = this.run(map);
			String sql = result.jdbcSql;
			List<Object> objs = result.jdbcPara;
			InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, true, objs,map);
			sql = ctx.getSql();
			objs = ctx.getParas();

			if (conn == null) {
				conn = sm.getDs().getConn(id, true, sql, objs);
			}

			if (this.sqlSource.getIdType() == DBStyle.ID_ASSIGN) {
				ps = conn.prepareStatement(sql);
			} else if (this.sqlSource.getIdType() == DBStyle.ID_AUTO) {
				ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else if(this.sqlSource.getIdType() == DBStyle.ID_SEQ) {
				CaseInsensitiveOrderSet idCols =  (CaseInsensitiveOrderSet)this.sqlSource.getTableDesc().getIdNames();
				if(idCols.size()!=1){
					throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR);
				}
				ps = conn.prepareStatement(sql, new String[]{idCols.getFirst()});
			}
			else {
				ps = conn.prepareStatement(sql);
			}

			this.setPreparedStatementPara(ps, objs);
			
			int ret = ps.executeUpdate();

			if (this.sqlSource.getIdType() == DBStyle.ID_AUTO||this.sqlSource.getIdType() == DBStyle.ID_SEQ) {
				ResultSet seqRs = ps.getGeneratedKeys();
				seqRs.next();
				Object key = seqRs.getObject(1);
				holder.setKey(key);
				seqRs.close();
			}
			this.callInterceptorAsAfter(ctx, ret);
			return ret;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
		}
	}

	public int insertBySqlId(Map  map, KeyHolder holder,String keyName) {
		
		boolean getKey = holder!=null ;
		
		PreparedStatement ps = null;
		Connection conn = null;
		try {

			SQLResult result = this.run(map);
			String sql = result.jdbcSql;
			List<Object> objs = result.jdbcPara;
			InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, true, objs,map);
			sql = ctx.getSql();
			objs = ctx.getParas();

			if (conn == null) {
				conn = sm.getDs().getConn(id, true, sql, objs);
			}
			
			if(getKey){
				ps = conn.prepareStatement(sql, new String[]{keyName});
			}else{
				ps = conn.prepareStatement(sql);
			}
			
			

			this.setPreparedStatementPara(ps, objs);
			
			int ret = ps.executeUpdate();
			
			if(getKey){
				ResultSet seqRs = ps.getGeneratedKeys();
				seqRs.next();
				Object key = seqRs.getObject(1);
				holder.setKey(key);
				seqRs.close();
			}
			
			this.callInterceptorAsAfter(ctx, ret);
			return ret;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
		}
	}

	public <T> T singleSelect(Object paras, Class<T> target) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		return this.selectSingle(map, target);
	}

	public <T> T selectSingle(Map<String, Object> map, Class<T> target) {

		List<T> result = select(target, map);

		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}
	
	public <T> T selectUnique(Map<String, Object> map, Class<T> target) {

		List<T> result = select(target, map);

		if (result.size() == 1) {
			return result.get(0);
		}else{
			throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，但数据库未找到结果集:参数是"+map);
		}
		
	}

	public <T> List<T> select(Class<T> clazz, Object paras) {
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
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, false, objs,paras);
		sql = ctx.getSql();
		objs = ctx.getParas();
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id, false, sql, objs);
			ps = conn.prepareStatement(sql);
			this.setPreparedStatementPara(ps, objs);
			
			
			rs = ps.executeQuery();

			if (mapper != null) {
				BeanProcessor beanProcessor = new BeanProcessor(this.sm.getNc(), this.sm);
				resultList = new RowMapperResultSetExt<T>(mapper, beanProcessor).handleResultSet(rs, clazz);
				this.callInterceptorAsAfter(ctx, resultList);

			} else {
				resultList = mappingSelect(rs, clazz);
				//1.5.0 feature 
				if(result.mapingEntrys!=null){
					for(MappingEntity mapConf:result.mapingEntrys){
						mapConf.map(resultList, sm);
					}
				}
			}

			this.callInterceptorAsAfter(ctx, resultList);
			return resultList;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(false, conn, ps, rs);
		}

	}

	public <T> List<T> select(Class<T> clazz, Map<String, Object> paras) {
		return this.select(clazz, paras, null);
	}

	public <T> List<T> mappingSelect(ResultSet rs, Class<T> clazz) throws SQLException {
		List<T> resultList = null;

		if (isBaseDataType(clazz)) { // 基本数据类型，如果有需要可以继续在isBaseDataType()添加
			resultList = new ArrayList<T>();
			
			while(rs.next()){
				T result = queryMapping.query(rs, new ScalarHandler<T>(clazz));
				resultList.add(result);
			}
			
			
		} else if (Map.class.isAssignableFrom(clazz)) { // 如果是Map的子类或者父类，返回List<Map<String,Object>>
			resultList = (List<T>) queryMapping.query(rs, new MapListHandler(this.sm.getNc(), this.sm,clazz));
		} else {
			resultList = queryMapping.query(rs, new BeanListHandler<T>(clazz, this.sm.getNc(), this.sm));
			
		}

		return resultList;

	}

	private static boolean isBaseDataType(Class<?> clazz) {
		return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class)
				|| clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class)
				|| clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class)
				|| clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class)
				|| clazz.isPrimitive());
	}

	public <T> List<T> select(Map<String, Object> paras, Class<T> mapping, RowMapper<T> mapper, long start, long size) {
		SQLScript pageScript = this.sm.getPageSqlScript(this.id);
		if (paras == null)
			paras = new HashMap<String, Object>();
		this.sm.getDbStyle().initPagePara(paras, start, size);
		return pageScript.select(mapping, paras, mapper);
		// return pageScript.se
	}

	public <T> List<T> select(Object paras, Class<T> mapping, RowMapper<T> mapper, long start, long end) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		return this.select(map, mapping, mapper, start, end);
	}

	public long selectCount(Object paras) {
		return this.singleSelect(paras, Long.class);
	}

	public long selectCount(Map<String, Object> paras) {
		return this.selectSingle(paras, Long.class);
	}

	public int update(Map<String, Object> paras) {

		SQLResult result = run(paras);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;

		InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, true, objs,paras);
		sql = ctx.getSql();
		objs = ctx.getParas();
		int rs = 0;
		PreparedStatement ps = null;
		// 执行jdbc
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id, true, sql, objs);
			ps = conn.prepareStatement(sql);
			this.setPreparedStatementPara(ps, objs);
			rs = ps.executeUpdate();
			this.callInterceptorAsAfter(ctx, rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
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
		InterceptorContext ctx = null;
		try {

			for (int k = 0; k < maps.length; k++) {
				Map<String, Object> paras = maps[k];
				SQLResult result = run(paras);
				List<Object> objs = result.jdbcPara;

				if (ps == null) {
					conn = sm.getDs().getConn(id, true, sql, objs);
					ps = conn.prepareStatement(result.jdbcSql);
					ctx = this.callInterceptorAsBefore(this.id, sql, true, Collections.EMPTY_LIST,paras);
				}
				this.setPreparedStatementPara(ps, objs);
				
				ps.addBatch();

			}
			rs = ps.executeBatch();
			this.callInterceptorAsAfter(ctx, rs);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
		}
		return rs;
	}
	
	

	public int[] updateBatch(List<?> list) {
		if(list.size()==0){
			return new int[0];
		}
		int[] rs = null;
		PreparedStatement ps = null;
		Connection conn = null;
		// 执行jdbc
		InterceptorContext ctx = null;
		try {

			for (int k = 0; k < list.size(); k++) {
				Map<String, Object> paras = new HashMap<String, Object>();
				paras.put("_root", list.get(k));
				SQLResult result = run(paras);
				List<Object> objs = result.jdbcPara;

				if (ps == null) {
					conn = sm.getDs().getConn(id, true, sql, objs);
					ps = conn.prepareStatement(result.jdbcSql);
					ctx = this.callInterceptorAsBefore(this.id, sql, true, Collections.emptyList(),paras);
				}

				this.setPreparedStatementPara(ps, objs);
				
				ps.addBatch();

			}
			rs = ps.executeBatch();
			this.callInterceptorAsAfter(ctx, rs);

		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
		}
		return rs;
	}

	
	public <T> T unique(Class<T> clazz, RowMapper<T> mapper, Object objId) {
		return single(clazz,mapper,objId,true);
	}
	
	public <T> T single(Class<T> clazz, RowMapper<T> mapper, Object objId) {
		return single(clazz,mapper,objId,false);
	}
	
	public <T> T single(Class<T> clazz, RowMapper<T> mapper, Object objId,boolean throwException) {

		MetadataManager mm = this.sm.getDbStyle().getMetadataManager();
		TableDesc table = mm.getTable(this.sm.getNc().getTableName(clazz));
		ClassDesc classDesc = table.getClassDesc(clazz, this.sm.getNc());
		Map<String, Object> paras = new HashMap<String, Object>();
		this.setIdsParas(classDesc, objId, paras);
		SQLResult result = run(paras);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;
		ResultSet rs = null;
		PreparedStatement ps = null;
		T model = null;
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, false, objs,paras);
		sql = ctx.getSql();
		objs = ctx.getParas();
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id, false, sql, objs);
			ps = conn.prepareStatement(sql);
			this.setPreparedStatementPara(ps, objs);
			rs = ps.executeQuery();
			try{
				model = queryMapping.query(rs, new BeanHandler<T>(clazz, this.sm.getNc(), this.sm,throwException));
				
			}catch(BeetlSQLException ex){
				
				if(ex.code==BeetlSQLException.UNIQUE_EXCEPT_ERROR){
					throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR,"unique查询"+table.getName()+",但数据库未找到结果集:主键是"+objId);
				}else{
					throw ex;
				}
			}
			this.callInterceptorAsAfter(ctx, model);
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(false, conn, ps, rs);
		}
		return model;
	}

	public int deleteById(Class<?> clazz, Object objId) {

		MetadataManager mm = this.sm.getDbStyle().getMetadataManager();
		TableDesc table = mm.getTable(this.sm.getNc().getTableName(clazz));
		ClassDesc classDesc = table.getClassDesc(clazz, this.sm.getNc());
		
		Map<String, Object> paras = new HashMap<String, Object>();
		this.setIdsParas(classDesc, objId, paras);

		SQLResult result = run(paras);
		String sql = result.jdbcSql;
		List<Object> objs = result.jdbcPara;

		InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, true, objs,paras);
		sql = ctx.getSql();
		objs = ctx.getParas();
		int rs = 0;
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id, true, sql, objs);
			ps = conn.prepareStatement(sql);
			this.setPreparedStatementPara(ps, objs);
			rs = ps.executeUpdate();
			this.callInterceptorAsAfter(ctx, rs);
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
		}
		return rs;
	}

	public <T> List<T> sqlReadySelect(Class<T> clazz, SQLReady p) {
		String sql = this.sql;
		List<Object> objs = Arrays.asList(p.getArgs());
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<T> resultList = null;
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, false, objs,this.getSQLReadyParas(objs));
		sql = ctx.getSql();
		objs = ctx.getParas();
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id, false, sql, objs);
			ps = conn.prepareStatement(sql);
			this.setPreparedStatementPara(ps, objs);
			rs = ps.executeQuery();
			resultList = mappingSelect(rs, clazz);

			this.callInterceptorAsAfter(ctx, resultList);
			return resultList;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(false, conn, ps, rs);
		}
	}

	public int sqlReadyExecuteUpdate(SQLReady p) {

		String sql = this.sql;
		List<Object> objs = Arrays.asList(p.args);
		InterceptorContext ctx = this.callInterceptorAsBefore(this.id, sql, true, objs,this.getSQLReadyParas(objs));
		sql = ctx.getSql();
		objs = ctx.getParas();
		int rs = 0;
		PreparedStatement ps = null;
		// 执行jdbc
		Connection conn = null;
		try {
			conn = sm.getDs().getConn(id, true, sql, objs);
			ps = conn.prepareStatement(sql);
			this.setPreparedStatementPara(ps, objs);
			rs = ps.executeUpdate();
			this.callInterceptorAsAfter(ctx, rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, ps);
		}
		return rs;
	}

	private void setPreparedStatementPara(PreparedStatement ps,List<Object> objs) throws SQLException {
		for (int i = 0; i < objs.size(); i++) {
			Object o = objs.get(i);
			// 兼容性修改：oralce 驱动 不识别util.Date
			if (o != null ) {
				Class c = o.getClass();
				if(c== java.util.Date.class){
					o = new Timestamp(((java.util.Date) o).getTime());
				}else if(Enum.class.isAssignableFrom(c)){
					o = EnumKit.getValueByEnum(o);
				}
//				 o =new java.sql.Date(((java.util.Date)o).getTime());
				
			}
			ps.setObject(i + 1, o);
		}
	}
	
	
	protected void clean(boolean isUpdate, Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (!this.sm.getDs().isTransaction()) {
				try {

					if (conn != null) {
						// colse 不一定能保证能自动commit
						if (isUpdate && !conn.getAutoCommit())
							conn.commit();
						conn.close();
					}
				} catch (SQLException e) {
					throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
				}

			}
		} catch (SQLException e) {
			// ignore
		}
	}

	protected void clean(boolean isUpdate, Connection conn, PreparedStatement ps) {
		this.clean(isUpdate, conn, ps, null);
	}
	
	protected void clean( Connection conn) {
		this.clean(true, conn, null, null);
	}

	private InterceptorContext callInterceptorAsBefore(String sqlId, String sql, 
				boolean isUpdate, List<Object> jdbcParas,Map<String,Object> inputParas) {

		InterceptorContext ctx = new InterceptorContext(sqlId, sql, jdbcParas,inputParas, isUpdate);
		for (Interceptor in : sm.inters) {
			in.before(ctx);
		}
		return ctx;
	}

	private void callInterceptorAsAfter(InterceptorContext ctx, Object result) {
		if (sm.inters == null)
			return;
		if (!ctx.isUpdate()) {
			if (result instanceof List) {
				List list = (List) result;
				ctx.setResult(list.size());
			} else {
				ctx.setResult(result);
			}

		} else {
			ctx.setResult(result);
		}
		for (Interceptor in : sm.inters) {
			in.after(ctx);
		}
		return;
	}
	
	private Map<String,Object> getSQLReadyParas(List<Object> paras){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("_root", paras);
		return map ;
	}

	public String getId() {
		return id;
	}
	
	/**
	 * 为主键设置参数
	 * @param desc
	 * @param obj
	 * @param paras
	 */
	private void setIdsParas(ClassDesc desc,Object obj,Map<String, Object> paras){
		List<String> idCols = desc.getIdCols();
		if(idCols.size()==1){
			paras.put(idCols.get(0), obj);
		}else{
			//来自对象id的属性.
			Map<String,Object> map = desc.getIdMethods();
			for(String idCol:idCols){
				Method m =  (Method)map.get(idCol);
				try{
					Object os = m.invoke(obj, new Object[0]);
					paras.put(idCol, os);
				}catch(Exception ex){
					throw new BeetlSQLException(BeetlSQLException.ID_VALUE_ERROR,"无法设置复合主键:"+idCol,ex);
				}
			}
		}
	}
	
	private void addParaIfAssignId(Map map){
		if (this.sqlSource.getIdType() == DBStyle.ID_ASSIGN&&sqlSource.getAssignIds()!=null) {
			Map<String, AssignID> ids = sqlSource.getAssignIds();
			for(Entry<String, AssignID> entry:ids.entrySet()){
				String attrName = entry.getKey();
				AssignID assignId = entry.getValue();
				String algorithm = assignId.value();
				String param = assignId.param();
				Object o = this.sm.getAssignIdByIdAutonGen(algorithm,param,sqlSource.getTableDesc().getName());
				map.put(attrName, o);
			}
			
		}
		
	}
	
	public String getSql() {
		return sql;
	}



}
