package org.beetl.sql.core.db;

import org.beetl.core.Configuration;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.annotatoin.DateTemplate;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.annotatoin.TableTemplate;
import org.beetl.sql.core.engine.Beetl;

import java.lang.reflect.Method;
import java.util.Set;
/**
 * 按照mysql来的，oralce需要重载insert，page方法
 * @author xiandafu
 *
 */
public abstract class AbstractDBStyle implements DBStyle {
	
	protected static AbstractDBStyle adbs;
	protected NameConversion nameConversion;
	protected MetadataManager metadataManager;
	public  String STATEMENT_START;// 定界符开始符号
	public  String STATEMENT_END;// 定界符结束符号
	public  String HOLDER_START;// 站位符开始符号
	public String HOLDER_END;// 站位符结束符号
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
		String appendSql = "";
		TableTemplate t = cls.getAnnotation(TableTemplate.class);
		if(t!=null){
			appendSql = t.value();
			if(appendSql==null||appendSql.length()==0){
				appendSql = " order by "+table.getMetaIdName()+" desc ";
			}
		}
		return new SQLSource(new StringBuilder("select * from ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(condition).append(appendSql).toString());
	}
	
	@Override
	public SQLSource genSelectCountByTemplate(Class<?> cls){
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		String condition = getSelectTemplate(cls);
		String appendSql = "";
		TableTemplate t = cls.getAnnotation(TableTemplate.class);
		if(t!=null){
			appendSql = t.value();
			if(appendSql==null){
				appendSql = " order by "+table.getMetaIdName()+" desc ";
			}
		}
		return new SQLSource(new StringBuilder("select count(1) from ").append(this.getEscapeForKeyWord()+table.getMetaName()+this.getEscapeForKeyWord()).append(condition).append(appendSql).toString());

	}
	
	private String getSelectTemplate(Class<?> cls){
		String condition = " where 1=1 " + lineSeparator;
		String tableName = nameConversion.getTableName(cls);
		TableDesc  table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
		Set<String> cols = classDesc.getInCols();
		for(String col:cols){
			if(classDesc.isDateType(col)){
				//todo, attr属性并不完全是这么转成getter方法的
				String getter = "get"+col.substring(0,1).toUpperCase()+col.substring(1);
				try {
					Method m = cls.getMethod(getter, new Class[]{});
					DateTemplate dateTemplate = m.getAnnotation(DateTemplate.class);
					if(dateTemplate==null) continue;
					String sql = this.genDateAnnotatonSql(dateTemplate,cls,col);				
					condition = condition + sql;
					continue ;
				} catch (Exception e) {
					//不可能发生
					throw new RuntimeException("获取metod出错"+e.getMessage());
				} 
				
			}else {
				condition = condition + appendWhere(cls,table, col);
			}
			if(col.equals(classDesc.getIdName())){
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
				condition = condition + appendWhere(cls,table, col);
				continue ;
			}
			sql.append(appendSetColumn(cls,table, col));
		}
		StringBuilder trimSql = new StringBuilder();
		
		trimSql.append(this.getSTATEMENTSTART()).append("trim(){\n").append(this.getSTATEMENTEND()).append("\n").append(sql);
		trimSql.append(this.getSTATEMENTSTART()).append("}\n").append(this.getSTATEMENTEND());
		sql = removeComma(trimSql, condition);
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

    /****
     * 根据table生成字段名列表
     * @param table
     * @return
     */
	@Override
    public String genColumnList(String table){
        Set<String> colSet = getCols(table);
        if(null == colSet || colSet.isEmpty()){
            return "";
        }
        StringBuilder cols = new StringBuilder();
        for(String col:colSet){
            cols.append(col).append(",");
        }
        return cols.deleteCharAt(cols.length()-1).toString();
    }

    /***
     * 获取字段集合
     * @param tableName
     * @return
     */
    public Set<String> getCols(String tableName){
        
        TableDesc table = this.metadataManager.getTable(tableName);
        return table.getMetaCols();
//        ClassDesc classDesc = table.getClassDesc(nameConversion);
//        return classDesc.getInCols();
    }

    /***
     * 生成通用条件语句 含有Empty判断
     * @param tableName
     * @return
     */
    @Override
    public String genCondition(String tableName){
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(nameConversion);
        Set<String> colSet = classDesc.getInCols();
        if(null == colSet || colSet.isEmpty()){
            return "";
        }
        StringBuilder condition = new StringBuilder();
        for(String col:colSet){
        		if(col.equals(table.getMetaIdName())){
				continue ;
			}
            condition.append(appendWhere(null,table,col));
        }
        return "1 = 1  \n" + condition.toString();
    }

    /***
     * 生成通用的col=property (示例：age=${age},name=${name}) 含有Empty判断
     * @param tableName
     * @return
     */
    @Override
    public String genColAssignProperty(String tableName){
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc( nameConversion);
        Set<String> colSet = classDesc.getInCols();
        if(null == colSet || colSet.isEmpty()){
            return "";
        }
        StringBuilder sql = new StringBuilder();
        for(String col:colSet){
            sql.append(appendSetColumn(null,table, col));
        }
        return sql.deleteCharAt(sql.length() - 1).toString();
    }

    /***
     * 生成通用的col=property (示例：age=${age},name=${name}) 没有Empty判断
     * @param tableName
     * @return
     */
    @Override
    public String genColAssignPropertyAbsolute(String tableName){
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc( nameConversion);
        Set<String> colSet = classDesc.getInCols();
        if(null == colSet || colSet.isEmpty()){
            return "";
        }
        StringBuilder sql = new StringBuilder();
        for(String col:colSet){
            sql.append(appendSetColumnAbsolute(null,table,col));
        }
        return sql.deleteCharAt(sql.length()-1).toString();
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
     * @param c
	 * @param table
	 * @param fieldName
	 * @return
	 */
	private String appendSetColumnAbsolute(Class<?> c,TableDesc table,String fieldName) {
		String colName = nameConversion.getColName(c,fieldName);
		return this.getEscapeForKeyWord()+colName +this.getEscapeForKeyWord()+ "="+HOLDER_START + fieldName + HOLDER_END+",";
	}
	
	/***
	 * 生成一个追加在set子句的后面sql(示例：name=${name},)有Empty判断
     * @param c
	 * @param table
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
	 * 生成一个追加在where子句的后面sql(示例：and name=${name} )
     * @param c
	 * @param table
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
	
	private String appendWhere(Class<?> c,String fieldName,String sql) {
		String prefix = "";		
		String colName = nameConversion.getColName(c,fieldName);
		String connector = " and ";
		return STATEMENT_START + "if(!isEmpty(" + prefix+fieldName + ")){"
		+ STATEMENT_END + connector + sql + lineSeparator + STATEMENT_START + "}" + STATEMENT_END;

	}
	
	/****
	 * 生成一个追加在insert into 子句的后面sql(示例：name,)
     * @param c
	 * @param table
	 * @param fieldName
	 * @return
	 */
	private String appendInsertColumn(Class<?> c,TableDesc table,String fieldName) {
		String colName = nameConversion.getColName(c,fieldName);
		return  this.getEscapeForKeyWord()+colName +this.getEscapeForKeyWord()+ ",";
	}
	
	/****
	 * 生成一个追加在insert into value子句的后面sql(示例：name=${name},)
	 * @param table
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
	
	private String genDateAnnotatonSql(DateTemplate t,Class c,String col){
		String accept = t.accept();
		String[] vars = null;
		if(accept==null||accept.length()==0){
			String col1 = col.substring(0,1).toUpperCase()+col.substring(1);
			vars = new String[]{DateTemplate.MIN_PREFIX+col1,DateTemplate.MAX_PREFIX+col1};
		}else{
			vars = t.accept().split(",");
		}
		
		
		String[] comp = null;
		String compare = t.compare();
		if(compare==null||compare.length()==0){
			comp = new String[]{DateTemplate.LARGE_OPT,DateTemplate.LESS_OPT};
			
		}else{
			comp = t.accept().split(",");
		}
		t.compare().split(",");
		
		String prefix = "";		
		
		String connector = " and ";
		String sql =  STATEMENT_START + "if(!isEmpty(" + prefix+vars[0] + ")){"
		+ STATEMENT_END + connector + col+comp[0] +this.HOLDER_START+vars[0]+HOLDER_END+ lineSeparator + STATEMENT_START + "}" + STATEMENT_END;
		
		sql =  sql+STATEMENT_START + "if(!isEmpty(" + prefix+vars[1] + ")){"
				+ STATEMENT_END + connector + col+comp[1] +this.HOLDER_START+vars[1]+HOLDER_END+ lineSeparator + STATEMENT_START + "}" + STATEMENT_END;
		return sql;
		
	}

}