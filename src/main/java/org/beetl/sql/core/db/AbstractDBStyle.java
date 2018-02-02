package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.core.Configuration;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SQLTableSource;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.DateTemplate;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.annotatoin.TableTemplate;
import org.beetl.sql.core.engine.Beetl;
import org.beetl.sql.core.kit.BeanKit;

/**
 * 按照mysql来的，oralce需要重载insert，page方法
 *
 * @author xiandafu
 */
public abstract class AbstractDBStyle implements DBStyle {

    protected static AbstractDBStyle adbs;
    protected NameConversion nameConversion;
    protected MetadataManager metadataManager;
    public String STATEMENT_START;// 定界符开始符号
    public String STATEMENT_END;// 定界符结束符号
    public String HOLDER_START;// 站位符开始符号
    public String HOLDER_END;// 站位符结束符号
    protected String lineSeparator = System.getProperty("line.separator", "\n");
    protected  KeyWordHandler  keyWordHandler= new DefaultKeyWordHandler();
    //翻页从0还是1开始，默认从1开始
    protected boolean offsetStartZero = false;

    public AbstractDBStyle() {

    }

    @Override
    public void init(Beetl beetl) {
        Configuration cf = beetl.getGroupTemplate().getConf();
        STATEMENT_START = cf.getStatementStart();
        STATEMENT_END = cf.getStatementEnd();
        if (STATEMENT_END == null || STATEMENT_END.length() == 0) {
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
        TableDesc table = this.metadataManager.getTable(tableName);
        String condition = appendIdCondition(cls);
        return new SQLTableSource(new StringBuilder("select * from ").append(getTableName(table)).append(condition).toString());
    }
    
    @Override
    public SQLSource genSelectByIdForUpdate(Class<?> cls){
    		SQLSource source = genSelectById(cls);
    		String template = source.getTemplate();
    		template = template+" for update";
    		source.setTemplate(template);
    		return source;
    }

    @Override
    public SQLSource genSelectByTemplate(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        String condition = getSelectTemplate(cls);
        String appendSql = "";
        TableTemplate t = (TableTemplate)BeanKit.getAnnotation(cls, TableTemplate.class);
        
        if (t != null) {
            appendSql = t.value();
            if ((appendSql == null || appendSql.length() == 0) && table.getIdNames().size() != 0) {

                appendSql = " order by ";
                Set<String> ids = table.getIdNames();
                int i = 0;
                for (String id : ids) {
                    appendSql += id + " desc";
                    if (i == (ids.size() - 1)) {
                        break;
                    }
                    appendSql += " , ";
                }

            }
        }
        String sql = new StringBuilder("select * from ").append(getTableName(table)).append(condition).append(appendSql).toString();
        return new SQLTableSource(sql);
    }

    @Override
    public SQLSource genSelectCountByTemplate(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        String condition = getSelectTemplate(cls);

        return new SQLTableSource(new StringBuilder("select count(1) from ").append(getTableName(table)).append(condition).toString());

    }

    protected String getSelectTemplate(Class<?> cls) {
        String condition = " where 1=1 " + lineSeparator;
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
        Iterator<String> cols = classDesc.getInCols().iterator();
        Iterator<String> attrs = classDesc.getAttrs().iterator();

        while (cols.hasNext() && attrs.hasNext()) {
            String col = cols.next();
            String attr = attrs.next();
            if (classDesc.isDateType(attr)) {
            		
                try {
                    DateTemplate dateTemplate = BeanKit.getAnnoation(classDesc.getTargetClass(), attr, DateTemplate.class);
                    if (dateTemplate == null){
                    		continue;
                    }
                    String sql = this.genDateAnnotatonSql(dateTemplate, cls, col);
                    condition = condition + sql;
                    continue;
                } catch (Exception e) {
                    //不可能发生
                    throw new RuntimeException("获取metod出错" + e.getMessage());
                }

            } else {
                condition = condition + appendWhere(cls, table, col, attr);
            }

//			condition = condition + appendWhere(cls,table, col);

        }
        return condition;
    }

    @Override
    public SQLSource genDeleteById(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        String condition = appendIdCondition(cls);

        return new SQLTableSource(new StringBuilder("delete from ").append(getTableName(table)).append(condition).toString());
    }

    @Override
    public SQLSource genSelectAll(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        tableName = table.getName();
        return new SQLTableSource(new StringBuilder("select * from ").append(getTableName(table)).toString());
    }

    @Override
    public SQLSource genUpdateById(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
        StringBuilder sql = new StringBuilder("update ").append(getTableName(table)).append(" set ").append(lineSeparator);
        Iterator<String> cols = classDesc.getInCols().iterator();
        Iterator<String> properties = classDesc.getAttrs().iterator();

        List<String> idCols = classDesc.getIdCols();
        while (cols.hasNext() && properties.hasNext()) {
            String col = cols.next();
            String prop = properties.next();
            
            if (classDesc.isUpdateIgnore(prop)) {
                continue;
            }
            if (idCols.contains(col)) {
                //主键不更新
                continue;
            }
            if(col.equals(classDesc.getVersionCol())){
            		//版本字段
            		sql.append(this.getKeyWordHandler().getCol(col)).append("=")
            		.append(this.getKeyWordHandler().getCol(col)).append("+1").append(",");
            		continue ;
            }
           

            sql.append(appendSetColumnAbsolute(cls, table, col, prop));
        }
        
        String condition = appendIdCondition(cls);
        condition = appendVersion(condition,classDesc);
        sql = removeComma(sql, condition);
        return new SQLTableSource(sql.toString());
    }

    @Override
    public SQLSource genUpdateAbsolute(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
        StringBuilder sql = new StringBuilder("update ").append(getTableName(table)).append(" set ").append(lineSeparator);
        Iterator<String> cols = classDesc.getInCols().iterator();
        Iterator<String> properties = classDesc.getAttrs().iterator();

        List<String> idCols = classDesc.getIdCols();
        while (cols.hasNext() && properties.hasNext()) {
            String col = cols.next();
            String prop = properties.next();

            if (classDesc.isUpdateIgnore(prop)) {
                continue;
            }
            if (idCols.contains(col)) {
                //主键不更新
                continue;
            }
            if(col.equals(classDesc.getVersionCol())){
                //版本字段
                sql.append(this.getKeyWordHandler().getCol(col)).append("=")
                        .append(this.getKeyWordHandler().getCol(col)).append("+1").append(",");
                continue ;
            }

            sql.append(appendSetColumnAbsolute(cls, table, col, prop));
        }

        sql = removeComma(sql, "");
        return new SQLTableSource(sql.toString());
    }

    private String appendVersion(String condition,ClassDesc desc){
    		String col = desc.getVersionCol();
    		if(col==null){
    			return condition;
    		}
    		String property = desc.getVersionProperty();
    		condition = condition+" and "+this.getKeyWordHandler().getCol(col)+" = "
                   +HOLDER_START+property+HOLDER_END;
    		return condition;
    }

    @Override
    public SQLSource genUpdateTemplate(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
        StringBuilder sql = new StringBuilder("update ").append(getTableName(table)).append(" set ").append(lineSeparator);
        String condition = appendIdCondition(cls);
        condition = appendVersion(condition,classDesc);
        Iterator<String> cols = classDesc.getInCols().iterator();
        Iterator<String> properties = classDesc.getAttrs().iterator();

        List<String> idCols = classDesc.getIdCols();
        while (cols.hasNext() && properties.hasNext()) {
            String col = cols.next();
            String prop = properties.next();
            if (classDesc.isUpdateIgnore(prop)) {
                continue;
            }
            if (idCols.contains(col)) {
                continue;
            }
            if(col.equals(classDesc.getVersionCol())){
	        		//版本字段
	        		sql.append(this.getKeyWordHandler().getCol(col)).append("=")
	        		.append(this.getKeyWordHandler().getCol(col)).append("+1").append(",");
	        		continue;
            }
            sql.append(appendSetColumn(cls, table, col, prop));
        }
        StringBuilder trimSql = new StringBuilder();

        trimSql.append(this.getSTATEMENTSTART()).append("trim(){\n").append(this.getSTATEMENTEND()).append("\n").append(sql);
        trimSql.append(this.getSTATEMENTSTART()).append("}\n").append(this.getSTATEMENTEND());
        sql = removeComma(trimSql, condition);
        if (condition == null) {
            throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR, "无法生成sql语句，缺少主键");
        }
        return new SQLTableSource(sql.toString());

    }

    @Override
    public SQLSource genUpdateAll(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
        StringBuilder sql = new StringBuilder("update ").append(getTableName(table)).append(" set ").append(lineSeparator);
        Iterator<String> cols = classDesc.getInCols().iterator();
        Iterator<String> properties = classDesc.getAttrs().iterator();

        List<String> idCols = classDesc.getIdCols();
        while (cols.hasNext() && properties.hasNext()) {
            String col = cols.next();
            String prop = properties.next();
            if (classDesc.isUpdateIgnore(prop)) {
                continue;
            }
            if (idCols.contains(col)) {
                //主键不更新
                continue;
            }
            sql.append(appendSetColumn(cls, table, col, prop));
        }
        sql = removeComma(sql, null);
        return new SQLTableSource(sql.toString());
    }

    @Override
    public SQLSource genInsert(Class<?> cls) {
    	return generalInsert(cls,false);
    }
    
    
    public SQLSource genInsertTemplate(Class<?> cls){
    	return generalInsert(cls,true);
    }
    

    
    protected SQLSource generalInsert(Class<?> cls,boolean template){
    	  String tableName = nameConversion.getTableName(cls);
          TableDesc table = this.metadataManager.getTable(tableName);
          ClassDesc classDesc = table.getClassDesc(cls, nameConversion);
          StringBuilder sql = new StringBuilder("insert into " + getTableName(table) + lineSeparator);
          StringBuilder colSql = new StringBuilder("(");
          StringBuilder valSql = new StringBuilder(" VALUES (");
          if(template){
        	  	//动态拼，需要使用trim去掉最后可能的空格
        	  	colSql.append(this.lineSeparator).append(this.STATEMENT_START);
        	  	colSql.append("trim({suffixOverrides:','}){").append(this.lineSeparator);
        	  	valSql.append(this.lineSeparator).append(this.STATEMENT_START);
        	  	valSql.append("trim({suffixOverrides:','}){").append(this.lineSeparator);
          }
          int idType = DBStyle.ID_ASSIGN;
          SQLTableSource source = new SQLTableSource();
          Iterator<String> cols = classDesc.getInCols().iterator();
          Iterator<String> attrs = classDesc.getAttrs().iterator();

          List<String> idCols = classDesc.getIdCols();
          while (cols.hasNext() && attrs.hasNext()) {
              String col = cols.next();
              String attr = attrs.next();
              if (classDesc.isInsertIgnore(attr)) {
                  continue;
              }

              if (idCols.size() == 1 && idCols.contains(col)) {

                  idType = this.getIdType(classDesc.getTargetClass(),attr);
                  if (idType == DBStyle.ID_AUTO) {
                      continue; //忽略这个字段
                  } else if (idType == DBStyle.ID_SEQ) {

                      colSql.append(appendInsertColumn(cls, table, col));
                      SeqID seqId = BeanKit.getAnnoation(classDesc.getTargetClass(), attr, 
                    		  (Method)classDesc.getIdMethods().get(attr), SeqID.class);
                      
                      valSql.append(seqId.name() + ".nextval,");
                      continue;
                  } else if (idType == DBStyle.ID_ASSIGN) {
                      //normal
                  }
              }

              if(template){
					colSql.append(appendInsertTemplateColumn(cls, table,attr, col));
					valSql.append(appendInsertTemplateValue(cls, table, attr));
              }else{
					colSql.append(appendInsertColumn(cls, table, col));
					valSql.append(appendInsertValue(cls, table, attr,col));
              }
              
          }
          
          if(template){
        	  	//结束trim(){}
        	  	colSql.append(this.lineSeparator).append(this.STATEMENT_START);
      	  	colSql.append("}").append(this.lineSeparator).append(this.STATEMENT_END);
      	  	colSql.append(")");
      	  	valSql.append(this.lineSeparator).append(this.STATEMENT_START);
      	  	valSql.append("}").append(this.lineSeparator).append(this.STATEMENT_END);
      	  	valSql.append(")");
      	  	sql.append(colSql).append(valSql);
          }else{
        	  	sql.append(removeComma(colSql, null).append(")").append(removeComma(valSql, null)).append(")").toString());
              
          }
          source.setTemplate(sql.toString());
          source.setIdType(idType);
          source.setTableDesc(table);
          if (idType == DBStyle.ID_ASSIGN) {
              Map<String, AssignID> map = new HashMap<String, AssignID>();
              for (String idAttr : classDesc.getIdAttrs()) {
                  Method getter = (Method) classDesc.getIdMethods().get(idAttr);
                  AssignID assignId = BeanKit.getAnnoation(classDesc.getTargetClass(), idAttr, getter,AssignID.class);
                  if (assignId != null && assignId.value().length() != 0) {

                      map.put(idAttr, assignId);
                  }
              }

              if (map.size() != 0) {
                  source.setAssignIds(map);
              }

          }

          return source;
    }

    /****
     * 根据table生成字段名列表
     *
     * @param table
     * @return
     */
    @Override
    public String genColumnList(String table) {
        Set<String> colSet = getCols(table);
        if (null == colSet || colSet.isEmpty()) {
            return "";
        }
        StringBuilder cols = new StringBuilder();
        for (String col : colSet) {
            cols.append(col).append(",");
        }
        return cols.deleteCharAt(cols.length() - 1).toString();
    }

    /***
     * 获取字段集合
     *
     * @param tableName
     * @return
     */
    public Set<String> getCols(String tableName) {

        TableDesc table = this.metadataManager.getTable(tableName);
        return table.getCols();
//        ClassDesc classDesc = table.getClassDesc(nameConversion);
//        return classDesc.getInCols();
    }

    /***
     * 生成通用条件语句 含有Empty判断
     *
     * @param tableName
     * @return
     */
    @Override
    public String genCondition(String tableName) {
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(nameConversion);
        Set<String> attrSet = classDesc.getAttrs();
        if (null == attrSet || attrSet.isEmpty()) {
            return "";
        }

        Iterator<String> attrIt = attrSet.iterator();
        Iterator<String> colIt = table.getCols().iterator();
        StringBuilder condition = new StringBuilder();
        Set<String> colsIds = table.getIdNames();
        while (colIt.hasNext() && attrIt.hasNext()) {
            String col = colIt.next();
            String attr = attrIt.next();
            if (colsIds.contains(col)) {
                continue;
            }
            condition.append(appendWhere(null, table, col, attr));
        }
        return "1 = 1  \n" + condition.toString();
    }

    /***
     * 生成通用的col=property (示例：age=${age},name=${name}) 含有Empty判断
     *
     * @param tableName
     * @return
     */
    @Override
    public String genColAssignProperty(String tableName) {
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(nameConversion);

        Iterator<String> cols = classDesc.getInCols().iterator();
        Iterator<String> properties = classDesc.getAttrs().iterator();

        StringBuilder sql = new StringBuilder();
        while (cols.hasNext() && properties.hasNext()) {
            String col = cols.next();
            String prop = properties.next();
            sql.append(appendSetColumn(null, table, col, prop));
        }

        return sql.deleteCharAt(sql.length() - 1).toString();
    }

    /***
     * 生成通用的col=property (示例：age=${age},name=${name}) 没有Empty判断
     *
     * @param tableName
     * @return
     */
    @Override
    public String genColAssignPropertyAbsolute(String tableName) {
        TableDesc table = this.metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(nameConversion);
        Set<String> colSet = classDesc.getInCols();
        Set<String> properties = classDesc.getAttrs();
        if (null == colSet || colSet.isEmpty()) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        Iterator<String> colIt = colSet.iterator();
        Iterator<String> propertiesIt = properties.iterator();

        while (colIt.hasNext() && propertiesIt.hasNext()) {
            String col = colIt.next();
            String prop = propertiesIt.next();
            sql.append(appendSetColumnAbsolute(null, table, col, prop));
        }
        return sql.deleteCharAt(sql.length() - 1).toString();
    }

 

    /****
     * 去掉逗号后面的加上结束符和条件并换行
     *
     * @param sql
     * @return
     */
    protected StringBuilder removeComma(StringBuilder sql, String condition) {
    	int index = sql.lastIndexOf(",");
    	if(index==-1){
    		//这种情况发生在没有列名，通常是 insert xxx () values()
    		return sql;
    	}else{
    		return sql.deleteCharAt(index).append((condition == null ? "" : condition));
    	}
        
    }

    /***
     * 生成一个追加在set子句的后面sql(示例：name=${name},)
     *
     * @param c
     * @param table
     * @param fieldName
     * @return
     */
    protected String appendSetColumnAbsolute(Class<?> c, TableDesc table, String colName, String fieldName) {
        return this.getKeyWordHandler().getCol(colName)  + "=" + HOLDER_START + fieldName + HOLDER_END + ",";
    }

    /***
     * 生成一个追加在set子句的后面sql(示例：name=${name},)有Empty判断
     *
     * @param c
     * @param table
     * @param fieldName
     * @return
     */
    protected String appendSetColumn(Class<?> c, TableDesc table, String colName, String fieldName) {
        String prefix = "";

        return STATEMENT_START + "if(!isEmpty(" + prefix + fieldName + ")){"
                + STATEMENT_END + "\t" + this.getKeyWordHandler().getCol(colName) + "=" + HOLDER_START + prefix + fieldName + HOLDER_END + ","
                + lineSeparator + STATEMENT_START + "}" + STATEMENT_END;


    }

    /*****
     * 生成一个追加在where子句的后面sql(示例：and name=${name} )
     *
     * @param c
     * @param table
     * @param fieldName
     * @return
     */
    protected String appendWhere(Class<?> c, TableDesc table, String colName, String fieldName) {
        String prefix = "";

        String connector = " and ";
        return STATEMENT_START + "if(!isEmpty(" + prefix + fieldName + ")){"
                + STATEMENT_END + connector + this.getKeyWordHandler().getCol(colName) + "=" + HOLDER_START + prefix + fieldName
                + HOLDER_END + lineSeparator + STATEMENT_START + "}" + STATEMENT_END;

    }


    /****
     * 生成一个追加在insert into 子句的后面sql(示例：name,)
     *
     * @param c
     * @param table
     * @param colName
     * @return
     */
    protected String appendInsertColumn(Class<?> c, TableDesc table, String colName) {
        return this.getKeyWordHandler().getCol(colName) + ",";
    }

    /****
     * 生成一个追加在insert into value子句的后面sql(示例：name=${name},)
     *
     * @param table
     * @param fieldName
     * @return
     */
    protected String appendInsertValue(Class<?> c, TableDesc table, String fieldName,String col) {
    	
        return HOLDER_START + fieldName + HOLDER_END + ",";

    }
    
    /****
     * 生成一个追加在insert into 子句的后面sql(示例：name,)
     * 需要判断值是否空，如果为空，则不作插入
     * @param c
     * @param table
     * @param fieldName
     * @param colName
     * @return
     */
    protected String appendInsertTemplateColumn(Class<?> c, TableDesc table,String fieldName, String colName) {
       
    	String col = this.getKeyWordHandler().getCol(colName);
    	if(col.startsWith("'")){
    		return HOLDER_START + "db.testColNull("+fieldName+",\""+col+"\")" + HOLDER_END  ;
    	}else{
    		return HOLDER_START + "db.testColNull("+fieldName+",'"+col+"')" + HOLDER_END  ;
    	}
    	
    }
    

    /****
     * 生成一个追加在insert into value子句的后面sql(示例：name=${name},)
     *需要判断值是否空，如果为空，则不作插入
     * @param table
     * @param fieldName
     * @return
     */
    protected String appendInsertTemplateValue(Class<?> c, TableDesc table, String fieldName) {
    	
    	 return HOLDER_START + "db.testNull("+fieldName+"!,\""+fieldName+"\")" + HOLDER_END ;

    }

    /***
     * 生成主键条件子句（示例 whrer 1=1 and id=${id}）
     *
     * @param cls
     * @return
     */
    protected String appendIdCondition(Class<?> cls) {
        String tableName = nameConversion.getTableName(cls);
        StringBuilder condition = new StringBuilder(" where ");
        TableDesc table = metadataManager.getTable(tableName);
        ClassDesc classDesc = table.getClassDesc(cls, nameConversion);

        List<String> colIds = classDesc.getIdCols();
        List<String> propertieIds = classDesc.getIdAttrs();
        this.checkId(colIds, propertieIds, cls.getName());
        Iterator<String> colIt = colIds.iterator();
        Iterator<String> propertieIt = propertieIds.iterator();
        if(colIt.hasNext() && propertieIt.hasNext()){
            String colId = colIt.next();
            String properId = propertieIt.next();
            condition.append(this.getKeyWordHandler().getCol(colId)).append(" = ")
                    .append(HOLDER_START).append(properId).append(HOLDER_END);
            while (colIt.hasNext() && propertieIt.hasNext()) {
                colId = colIt.next();
                properId = propertieIt.next();
                condition.append(" and ").append(this.getKeyWordHandler().getCol(colId)).append(" = ")
                        .append(HOLDER_START).append(properId).append(HOLDER_END);
            }
        }

        return condition.toString();
    }



    /****
     * 方法是否能用来生成select语句
     *
     * @param method
     * @return
     */
    protected boolean isLegalSelectMethod(Method method) {

        return method.getDeclaringClass() != Object.class
                && (method.getName().startsWith("get") || method.getName().startsWith("is"))
                && !java.util.Date.class.isAssignableFrom(method.getReturnType())
                && !java.util.Calendar.class.isAssignableFrom(method.getReturnType());
    }

    /****
     * 方法是否能用来生成select之外的语句，如update，insert
     *
     * @param method
     * @return
     */
    protected boolean isLegalOtherMethod(Method method) {
        return method.getDeclaringClass() != Object.class &&
                (method.getName().startsWith("get") || method.getName().startsWith("is"))
                && method.getParameterTypes().length == 0;
    }

    protected String genDateAnnotatonSql(DateTemplate t, Class c, String col) {
        String accept = t.accept();
        String[] vars = null;
        if (accept == null || accept.length() == 0) {
            String col1 = col.substring(0, 1).toUpperCase() + col.substring(1);
            vars = new String[]{DateTemplate.MIN_PREFIX + col1, DateTemplate.MAX_PREFIX + col1};
        } else {
            vars = t.accept().split(",");
        }


        String[] comp = null;
        String compare = t.compare();
        if (compare == null || compare.length() == 0) {
            comp = new String[]{DateTemplate.LARGE_OPT, DateTemplate.LESS_OPT};

        } else {
            comp =  t.compare().split(",");
        }

        String prefix = "";

        String connector = " and ";
        String sql = STATEMENT_START + "if(!isEmpty(" + prefix + vars[0] + ")){"
                + STATEMENT_END + connector + col + comp[0] + this.HOLDER_START + vars[0] + HOLDER_END + lineSeparator + STATEMENT_START + "}" + STATEMENT_END;

        sql = sql + STATEMENT_START + "if(!isEmpty(" + prefix + vars[1] + ")){"
                + STATEMENT_END + connector + col + comp[1] + this.HOLDER_START + vars[1] + HOLDER_END + lineSeparator + STATEMENT_START + "}" + STATEMENT_END;
        return sql;

    }

    protected String getTableName(TableDesc desc) {
        if (desc.getSchema() != null) {
            return this.getKeyWordHandler().getTable(desc.getSchema())+ "." + this.getKeyWordHandler().getTable(desc.getName()) ;
        } else {
            return this.getKeyWordHandler().getTable(desc.getName());
        }

    }

    protected void checkId(Collection colsId, Collection attrsId, String clsName) {
        if (colsId.size() == 0 || attrsId.size() == 0) {
            throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND, "主键未发现," + clsName+",检查数据库表定义或者NameConversion");
        }
    }

    protected String getOrderBy() {
        return lineSeparator + HOLDER_START + "text(has(_orderBy)?' order by '+_orderBy)" + HOLDER_END + " ";
    }

    /* 根据注解来决定主键采用哪种方式生成。在跨数据库应用中，可以为一个id指定多个注解方式，如mysql，postgres 用auto，oracle 用seq
     */
    @Override
    public int getIdType(Class c,String idProperty) {
        List<Annotation> ans = BeanKit.getAllAnnoation(c, idProperty);
        int idType = DBStyle.ID_AUTO; //默认是自增长

        for (Annotation an : ans) {
            if (an instanceof AutoID) {
                idType = DBStyle.ID_AUTO;
                break;// 优先
            } else if (an instanceof SeqID) {
                //my sql not support
            } else if (an instanceof AssignID) {
                idType = DBStyle.ID_ASSIGN;
            }
        }

        return idType;

    }

    @Override
    public KeyWordHandler getKeyWordHandler(){
    	return this.keyWordHandler;
    }
	public void setKeyWordHandler(KeyWordHandler keyWordHandler){
		this.keyWordHandler = keyWordHandler;
	}

}
