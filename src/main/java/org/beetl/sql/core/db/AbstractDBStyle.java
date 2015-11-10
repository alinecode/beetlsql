package org.beetl.sql.core.db;

import java.lang.reflect.Method;
import java.util.Set;

import org.beetl.core.Configuration;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.engine.Beetl;
import org.beetl.sql.core.kit.StringKit;
/**
 * 按照mysql来的，oralce需要重载insert，page方法
 * @author xiandafu
 *
 */
public abstract class AbstractDBStyle implements DBStyle {
	
	protected static AbstractDBStyle adbs;
	protected NameConversion nameConversion;
	protected MetadataManager metadataManager;
	protected String STATEMENT_START;// 定界符开始符号
	protected String STATEMENT_END;// 定界符结束符号
	protected String HOLDER_START;// 站位符开始符号
	protected String HOLDER_END;// 站位符结束符号
	protected String lineSeparator = System.getProperty("line.separator", "\n");
	//翻页从0还是1开始，默认从1开始
	protected boolean offsetStartZero = false ;
	
	public AbstractDBStyle() {
	
	}
	
	@Override
	public void init(Beetl beetl){
		Configuration cf =beetl.getGroupTemplate().getConf();
		STATEMENT_START = cf.getStatementStart();
		STATEMENT_END = cf.getStatementEnd();
		if(STATEMENT_END==null || STATEMENT_END.length()==0){
			STATEMENT_END = lineSeparator;
		}
		HOLDER_START = cf.getPlaceholderStart();
		HOLDER_END = cf.getPlaceholderEnd();
		offsetStartZero = Boolean.parseBoolean(beetl.getPs().getProperty("OFFSET_START_ZERO").trim());
	}

	public String getSTATEMENTSTART() {
		return STATEMENT_START;
	}

	public String getSTATEMENTEND() {
		return STATEMENT_END;
	}
	
	@Override
	public NameConversion getNameConversion() {
		return nameConversion;
	}

	@Override
	public void setNameConversion(NameConversion nameConversion) {
		this.nameConversion = nameConversion;
	}

	@Override
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	@Override
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	@Override
	public SQLSource genSelectById(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		String condition = appendIdCondition(cls);
		return new SQLSource(new StringBuilder("select * from ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).append(condition).toString());
	}

	@Override
	public SQLSource genSelectByTemplate(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		String condition = getSelectTemplate(cls);
		return new SQLSource(new StringBuilder("select * from ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(condition).toString());
	}
	
	@Override
	public SQLSource genSelectCountByTemplate(Class<?> cls){
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		String condition = getSelectTemplate(cls);
		return new SQLSource(new StringBuilder("select count(1) from ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(condition).toString());

	}
	
	private String getSelectTemplate(Class<?> cls){
		String condition = " where 1=1 " + lineSeparator;
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
		Set<String> cols = classDesc.getInCols();
		for(String col:cols){
			if(classDesc.isDateType(col)){
				continue ;
			}
			condition = condition + appendWhere(cls,table, col);
		}
		return condition;
	}

	@Override
	public SQLSource genDeleteById(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		String condition = appendIdCondition(cls);
		
		return new SQLSource(new StringBuilder("delete from ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(condition).toString());
	}

	@Override
	public SQLSource genSelectAll(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);		
		TableDesc table = this.metadataManager.getTable(tableName);
		tableName = table.getMetaName();
		return new SQLSource(new StringBuilder("select * from ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).toString());
	}

	@Override
	public SQLSource genUpdateById(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
		StringBuilder sql = new StringBuilder("update ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(" set ").append(lineSeparator);
		Set<String> cols = classDesc.getInCols();
		for(String col:cols){
			if(classDesc.getIdName().equals(col)){
				//主键不更新
				continue ;
			}
			
			sql.append(appendSetColumnAbsolute(cls,table, col));
		}
		
		String condition = appendIdCondition(cls);
		sql = removeComma(sql, condition);
		return new SQLSource(sql.toString());
	}
	
	@Override
	public SQLSource genUpdateTemplate (Class<?> cls) {
		
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
		StringBuilder sql = new StringBuilder("update ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(" set ").append(lineSeparator);
		String condition = " where 1=1 " + lineSeparator;
		
		Set<String> cols = classDesc.getInCols();
		for(String col:cols){
			if(classDesc.getIdName().equals(col)){
				//主键不更新
				continue ;
			}
			
			sql.append(appendSetColumn(cls,table, col));
			condition = condition + appendWhere(cls,table, col);
		}
		sql = removeComma(sql, condition);
		return new SQLSource(sql.toString());
		
	}

	@Override
	public SQLSource genUpdateAll(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);	
		StringBuilder sql = new StringBuilder("update ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(" set ").append(lineSeparator);
		Set<String> cols = classDesc.getInCols();
		for(String col:cols){
			if(classDesc.getIdName().equals(col)){
				//主键不更新
				continue ;
			}			
			sql.append(appendSetColumn(cls,table, col));
		}
		sql = removeComma(sql, null);
		return new SQLSource(sql.toString());
	}

	@Override
	public SQLSource genInsert(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);	
		StringBuilder sql = new StringBuilder("insert into " + this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord() + lineSeparator);
		StringBuilder colSql = new StringBuilder("(");
		StringBuilder valSql = new StringBuilder(" VALUES (");
		int idType = DBStyle.ID_ASSIGN ;
		SQLSource source = new SQLSource();
		Set<String> cols = classDesc.getInCols();
		for(String col:cols){
			if(col.equals(classDesc.getIdName())){				
				idType = this.getIdType(classDesc.getIdMethod());
				if(idType==DBStyle.ID_AUTO){
					continue ; //忽略这个字段
				}else if(idType==DBStyle.ID_SEQ){
					
					colSql.append(appendInsertColumn(cls,table, col));
					valSql.append( HOLDER_START+ "_tempKey" + HOLDER_END+",");
					SeqID seqId = classDesc.getIdMethod().getAnnotation(SeqID.class);
					source.setSeqName(seqId.name());
					continue;
				}else if(idType==DBStyle.ID_ASSIGN){
					//normal
				}
			}
			colSql.append(appendInsertColumn(cls,table, col));
			valSql.append(appendInsertVlaue(cls,table, col));
		}

		sql.append(removeComma(colSql, null).append(")").append(removeComma(valSql, null)).append(")").toString());
		source.setTemplate(sql.toString());
		source.setIdType(idType);
		
		 
		return source;
	}
	
	public String getEscapeForKeyWord(){
		return "\"";
	}
	
	/****
	 * 去掉逗号后面的加上结束符和条件并换行
	 * 
	 * @param sql
	 * @return
	 */
	private StringBuilder removeComma(StringBuilder sql, String condition) {
		return sql.deleteCharAt(sql.lastIndexOf(",")).append((condition == null ? "" : condition));
	}

	/***
	 * 生成一个追加在set子句的后面sql(示例：name=${name},)
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendSetColumnAbsolute(Class<?> c,TableDesc table,String fieldName) {
		String colName = nameConversion.getColName(c,fieldName);
		return this.getEscapeForKeyWord()+colName +this.getEscapeForKeyWord()+ "="+HOLDER_START + fieldName + HOLDER_END+",";
	}
	
	/***
	 * 生成一个追加在set子句的后面sql(示例：name=${name},)有Empty判断
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendSetColumn(Class<?> c,TableDesc table,String fieldName) {
		String prefix = "";
	
		String colName = nameConversion.getColName(c,fieldName);			
		return STATEMENT_START + "if(!isEmpty(" + prefix+fieldName + ")){"
				+ STATEMENT_END + "\t" + this.getEscapeForKeyWord()+colName+this.getEscapeForKeyWord() + "="+HOLDER_START + prefix+fieldName + HOLDER_END+","
				+ lineSeparator + STATEMENT_START + "}" + STATEMENT_END;
		
		
	}
	
	/*****
	 * 生成一个追加在where子句的后面sql(示例：name=${name} and)
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendWhere(Class<?> c,TableDesc table,String fieldName) {
		String prefix = "";
		
		String colName = nameConversion.getColName(c,fieldName);
		String connector = " and ";
		return STATEMENT_START + "if(!isEmpty(" + prefix+fieldName + ")){"
		+ STATEMENT_END + connector + this.getEscapeForKeyWord()+colName+this.getEscapeForKeyWord() + "="+HOLDER_START + prefix+fieldName
		+ HOLDER_END+ lineSeparator + STATEMENT_START + "}" + STATEMENT_END;

	}
	
	/****
	 * 生成一个追加在insert into 子句的后面sql(示例：name,)
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendInsertColumn(Class<?> c,TableDesc table,String fieldName) {
		String colName = nameConversion.getColName(c,fieldName);
		return  this.getEscapeForKeyWord()+colName +this.getEscapeForKeyWord()+ ",";
	}
	
	/****
	 * 生成一个追加在insert into value子句的后面sql(示例：name=${name},)
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendInsertVlaue(Class<?> c,TableDesc table,String fieldName) {
		
		return  HOLDER_START+ fieldName + HOLDER_END+",";
		
	}
	
	/***
	 * 生成主键条件子句（示例 whrer 1=1 and id=${id}）
	 * @param cls
	 * @return
	 */
	private String appendIdCondition(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		String condition = null;
		TableDesc  table = metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls,nameConversion );
		if(table.getMetaIdName()!=null){		
			condition = " where 1=1";		
			condition = condition + " and " + this.getEscapeForKeyWord()+table.getIdName()+this.getEscapeForKeyWord()+ "= "+HOLDER_START
					+ classDesc.getIdName()
					+ HOLDER_END;
	
		}else{
			throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND,"ID NOT FOUND");
		}
		return condition;
	}
	
	/****
	 * 生成一个循环读取Id列表
	 * @param tableName
	 * @param fieldName
	 * @return
	 
	private String appendIdList(String idName) {
		return new StringBuilder(lineSeparator).append(STATEMENT_START)
				.append("trim(){for(obj in map){").append(STATEMENT_END)
				.append(HOLDER_START+ "obj."+idName + HOLDER_END+",").append(lineSeparator)
				.append(STATEMENT_START).append("}}").append(STATEMENT_END).toString();
	}
	*/
	
	/****
	 * 方法是否能用来生成select语句
	 * @param method
	 * @return
	 */
	private boolean isLegalSelectMethod(Method method){
		
		return method.getDeclaringClass() != Object.class 
				&& (method.getName().startsWith("get") || method.getName().startsWith("is"))
				&& !java.util.Date.class.isAssignableFrom(method.getReturnType())	
				&& !java.util.Calendar.class.isAssignableFrom(method.getReturnType());
	}
	
	/****
	 * 方法是否能用来生成select之外的语句，如update，insert
	 * @param method
	 * @return
	 */
	private boolean isLegalOtherMethod(Method method){
		return method.getDeclaringClass() != Object.class &&
                (method.getName().startsWith("get")||method.getName().startsWith("is"))
                && method.getParameterTypes().length == 0;
	}

}