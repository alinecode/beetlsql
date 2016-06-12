package org.beetl.sql.core.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.SeqID;

public class OracleStyle extends AbstractDBStyle {

	public OracleStyle() {
	}
	
	@Override
	public String getPageSQL(String sql) {
		//beetlT，beetl_rn 避免与sql重复
		String pageSql = "SELECT * FROM "
		+" ( "
		+" SELECT beeltT.*, ROWNUM beetl_rn "
		+" FROM ( \n" +sql+ this.getOrderBy()+"\n )  beeltT " 
		+" WHERE ROWNUM <"+HOLDER_START+DBStyle.PAGE_END+HOLDER_END
		+") "
		+"WHERE beetl_rn >= " +HOLDER_START+DBStyle.OFFSET+HOLDER_END ;
		return pageSql;
	}

	@Override
	public void initPagePara(Map<String, Object> paras,long start,long size) {
		long s = start+(this.offsetStartZero?1:0);
		paras.put(DBStyle.OFFSET,s);
		paras.put(DBStyle.PAGE_END,s+size);
	}

	@Override
	public int getIdType(Method idMethod) {
		Annotation[] ans = idMethod.getAnnotations();
		int idType = DBStyle.ID_ASSIGN; // 默认是自增长

		for (Annotation an : ans) {
			if (an instanceof SeqID) {
				idType = DBStyle.ID_SEQ;
				//seq 总是优先
				break ;
			} else if (an instanceof AssignID) {
				idType = DBStyle.ID_ASSIGN;
			}
		}

		return idType;

	}

	@Override
	public String getName() {
		return "oracle";
	}
	
	@Override
	public String getEscapeForKeyWord(){
		return "";
	}
	
	@Override
	public SQLSource genInsert(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);	
		StringBuilder sql = new StringBuilder("insert into " + getTableName(table) + lineSeparator);
		StringBuilder colSql = new StringBuilder("(");
		StringBuilder valSql = new StringBuilder(" VALUES (");
		int idType = DBStyle.ID_ASSIGN ;
		SQLSource source = new SQLSource();
		Set<String> cols = classDesc.getInCols();
		List<String> idCols = classDesc.getIdNames();
		for(String col:cols){
			if(idCols.contains(col)){					
				idType = idCols.size()!=1?DBStyle.ID_ASSIGN:this.getIdType(classDesc.getIdMethods().get(col));
				
				if(idType==DBStyle.ID_AUTO){
					continue ; //忽略这个字段
				}else if(idType==DBStyle.ID_SEQ){
					
					colSql.append(appendInsertColumn(cls,table, col));
					SeqID seqId = classDesc.getIdMethods().get(col).getAnnotation(SeqID.class);
					source.addIdCol(col);
					
					valSql.append( seqId.name()+".nextval,");
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

}
