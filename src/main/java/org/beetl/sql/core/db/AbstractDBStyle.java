package org.beetl.sql.core.db;

import java.lang.reflect.Method;

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
		String fieldName = null;
		String condition = " where 1=1 " + lineSeparator;
		Method[] methods = cls.getMethods();
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		for (Method method : methods) {
			if(isLegalSelectMethod(method)){
				fieldName = StringKit.toLowerCaseFirstOne(method.getName().substring(3));
				condition = condition + appendWhere(cls,table, fieldName);
			}
		}
		return new SQLSource(new StringBuilder("select * from ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).append(condition).toString());
	}
	
	@Override
	public SQLSource genSelectCountByTemplate(Class<?> cls){
		String fieldName = null;
		String condition = " where 1=1 " + lineSeparator;
		Method[] methods = cls.getMethods();
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		for (Method method : methods) {
				if(isLegalSelectMethod(method)){
					fieldName = StringKit.toLowerCaseFirstOne(method.getName().substring(3));
					condition = condition + appendWhere(cls,table, fieldName);
				}
		}
		return new SQLSource(new StringBuilder("select count(*) from ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).append(condition).toString());

	}

	@Override
	public SQLSource genDeleteById(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		String condition = appendIdCondition(cls);
		
		return new SQLSource(new StringBuilder("delete from ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).append(condition).toString());
	}

	@Override
	public SQLSource genSelectAll(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		
		return new SQLSource(new StringBuilder("select * from ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).toString());
	}

	@Override
	public SQLSource genUpdateById(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		StringBuilder sql = new StringBuilder("update ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).append(" set ").append(lineSeparator);
		String fieldName = null;
		String id = metadataManager.getIds(tableName);
		String idAttrName = nameConversion.getPropertyName(cls,id);
		
		Method[] methods = cls.getMethods();
		for (Method method : methods) {
				if(isLegalOtherMethod(method) ){
					fieldName = StringKit.toLowerCaseFirstOne(method.getName().substring(3));
					if(fieldName.equals(idAttrName)){
						//主键不需要更改
						continue ;
					}
					sql.append(appendSetColumnAbsolute(cls,table, fieldName));
				}
		}
		String condition = appendIdCondition(cls);
		sql = removeComma(sql, condition);
		return new SQLSource(sql.toString());
	}
	
	@Override
	public SQLSource genUpdateTemplate (Class<?> cls) {
		//SQLPart s = new SQLPart();
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		StringBuilder sql = new StringBuilder("update ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).append(" set ").append(lineSeparator);
		String fieldName = null;
		String condition = " where 1=1 " + lineSeparator;
		Method[] methods = cls.getMethods();
		for (Method method : methods) {
				if(isLegalOtherMethod(method)){
					fieldName = StringKit.toLowerCaseFirstOne(method.getName().substring(3));
					sql.append(appendSetColumn(cls,table, fieldName,"para"));
					condition = condition + appendWhere(cls,table, fieldName,"condition");
				}
		}
		sql = removeComma(sql, condition);
		return new SQLSource(sql.toString());
	}

	@Override
	public SQLSource genUpdateAll(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		StringBuilder sql = new StringBuilder("update ").append(this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord()).append(" set ").append(lineSeparator);
		String fieldName = null;
		Method[] methods = cls.getMethods();
		for (Method method : methods) {
			if(isLegalOtherMethod(method)){
				fieldName = StringKit.toLowerCaseFirstOne(method.getName().substring(3));
				sql.append(appendSetColumn(cls,table, fieldName));
			}
		}
		sql = removeComma(sql, null);
		return new SQLSource(sql.toString());
	}

	@Override
	public SQLSource genInsert(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		StringBuilder sql = new StringBuilder("insert into " + this.getEscapeForKeyWord()+tableName+this.getEscapeForKeyWord() + lineSeparator);
		StringBuilder colSql = new StringBuilder("(");
		StringBuilder valSql = new StringBuilder(" VALUES (");
		String fieldName = null;
		int idType = DBStyle.ID_ASSIGN ;
		SQLSource source = new SQLSource();
		Method[] methods = cls.getMethods();
		for (Method method : methods) {
			if(isLegalOtherMethod(method)){
				fieldName = StringKit.toLowerCaseFirstOne(method.getName().substring(3));
				String id = this.metadataManager.getIds(tableName);
				if(id.equals(fieldName)){
					idType = this.getIdType(method);
					if(idType==DBStyle.ID_AUTO){
						continue ; //忽略这个字段
					}else if(idType==DBStyle.ID_SEQ){
						
						colSql.append(appendInsertColumn(cls,table, fieldName));
						valSql.append( HOLDER_START+ "_tempKey" + HOLDER_END+",");
						SeqID seqId = method.getAnnotation(SeqID.class);
						source.setSeqName(seqId.name());
						continue;
					}else if(idType==DBStyle.ID_ASSIGN){
						//normal
					}
				}
				colSql.append(appendInsertColumn(cls,table, fieldName));
				valSql.append(appendInsertVlaue(cls,table, fieldName));
			}
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
		if (metadataManager.existColName(table, colName)) {
			return this.getEscapeForKeyWord()+colName +this.getEscapeForKeyWord()+ "="+HOLDER_START + fieldName + HOLDER_END+",";
		}
		return "";
	}
	
	/***
	 * 生成一个追加在set子句的后面sql(示例：name=${name},)有Empty判断
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendSetColumn(Class<?> c,TableDesc table,String fieldName,String...prefixs) {
		String prefix = "";
		if(prefixs.length > 0){
			prefix = prefixs[0]+".";
		}
		String colName = nameConversion.getColName(c,fieldName);
		if (metadataManager.existColName(table, colName)) {
			return STATEMENT_START + "if(!isEmpty(" + prefix+fieldName + ")){"
					+ STATEMENT_END + "\t" + this.getEscapeForKeyWord()+colName+this.getEscapeForKeyWord() + "="+HOLDER_START + prefix+fieldName + HOLDER_END+","
					+ lineSeparator + STATEMENT_START + "}" + STATEMENT_END;
		}
		return "";
	}
	
	/*****
	 * 生成一个追加在where子句的后面sql(示例：name=${name} and)
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendWhere(Class<?> c,TableDesc table,String fieldName,String...prefixs) {
		String prefix = "";
		if(prefixs.length > 0){
			prefix = prefixs[0]+".";
		}
		String colName = nameConversion.getColName(c,fieldName);
		String connector = " and ";
		if (metadataManager.existColName(table, colName)) {
			return STATEMENT_START + "if(!isEmpty(" + prefix+fieldName + ")){"
					+ STATEMENT_END + connector + this.getEscapeForKeyWord()+colName+this.getEscapeForKeyWord() + "="+HOLDER_START + prefix+fieldName
					+ HOLDER_END+ lineSeparator + STATEMENT_START + "}" + STATEMENT_END;
		}
		return "";
	}
	
	/****
	 * 生成一个追加在insert into 子句的后面sql(示例：name,)
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendInsertColumn(Class<?> c,TableDesc table,String fieldName) {
		String colName = nameConversion.getColName(c,fieldName);
		if (metadataManager.existColName(table, colName)) {
			return  this.getEscapeForKeyWord()+colName +this.getEscapeForKeyWord()+ ",";
		}
		return "";
	}
	
	/****
	 * 生成一个追加在insert into value子句的后面sql(示例：name=${name},)
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	private String appendInsertVlaue(Class<?> c,TableDesc table,String fieldName) {
		String colName = nameConversion.getColName(c,fieldName);
		if (metadataManager.existColName(table, colName)) {
			return  HOLDER_START+ fieldName + HOLDER_END+",";
		}
		return "";
	}
	
	/***
	 * 生成主键条件子句（示例 whrer 1=1 and id=${id}）
	 * @param cls
	 * @return
	 */
	private String appendIdCondition(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		String condition = null;
		String id = metadataManager.getIds(tableName);
		if(id!=null){
			String attrName = null;
			condition = " where 1=1";
			attrName = nameConversion.getPropertyName(cls,id);
			if (metadataManager.existPropertyName(cls, attrName)) {
				condition = condition + " and " + this.getEscapeForKeyWord()+id+this.getEscapeForKeyWord()+ "= "+HOLDER_START
						+ attrName
						+ HOLDER_END;
			}
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
				&& method.getName().startsWith("get")
				&& !java.util.Date.class.isAssignableFrom(method.getReturnType())	
				&& !java.util.Calendar.class.isAssignableFrom(method.getReturnType());
	}
	
	/****
	 * 方法是否能用来生成select之外的语句，如update，insert
	 * @param method
	 * @return
	 */
	private boolean isLegalOtherMethod(Method method){
		return method.getDeclaringClass() != Object.class && method.getName().startsWith("get");
	}

}