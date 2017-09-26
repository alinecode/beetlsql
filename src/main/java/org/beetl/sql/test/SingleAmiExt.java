package org.beetl.sql.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.ClassDesc;
import org.beetl.sql.core.db.MetadataManager;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.mapper.MapperInvoke;

public class SingleAmiExt implements MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		if (args.length == 1) {
			return sm.single(entityClass, args[0]);
		}
		List attrs = (List) args[1];
		String sql  = getSingleSelect(entityClass,sm,attrs);
	
		Map paras = this.setIdsParas(sm, args[0], entityClass);
		List list = sm.execute(sql, entityClass, paras);
		if(list.size()==0) {
			return null;
		}else {
			return list.get(0);
		}

	}

	/*设置主键，参考SQLScript的setIdsParas方法*/
	private Map setIdsParas(SQLManager sm, Object key, Class entityClass) {
		AbstractDBStyle style = (AbstractDBStyle) sm.getDbStyle();
		MetadataManager metadataManager = sm.getMetaDataManager();
		NameConversion nameConversion = sm.getNc();
		String tableName = nameConversion.getTableName(entityClass);

		TableDesc table = metadataManager.getTable(tableName);
		ClassDesc desc = table.getClassDesc(entityClass, nameConversion);
		Map paras = new HashMap();
		List<String> idAttrs = desc.getIdAttrs();
		if (idAttrs.size() == 1) {
			paras.put(idAttrs.get(0), key);
		} else {
			// 来自对象id的属性.复合主键
			Map<String, Object> map = desc.getIdMethods();
			for (int i = 0; i < idAttrs.size(); i++) {
				String idCol = idAttrs.get(i);
				String idAttr = idAttrs.get(i);
				Method m = (Method) map.get(idAttr);
				try {
					Object os = m.invoke(key, new Object[0]);
					paras.put(idAttr, os);
				} catch (Exception ex) {
					throw new BeetlSQLException(BeetlSQLException.ID_VALUE_ERROR, "无法设置复合主键:" + idCol, ex);
				}
			}

		}
		return paras;
	}

	/**
	 * 获取sql语句
	 * 
	 * @param cls
	 * @param sm
	 * @param attrs
	 * @return
	 */
	private String getSingleSelect(Class cls, SQLManager sm, List attrs) {
		NameConversion nameConversion = sm.getNc();
		String condition = appendIdCondition(sm, cls);
		StringBuilder sb = new StringBuilder("select ");

		for (Object o : attrs) {
			String attr = (String) o;
			String col = nameConversion.getColName(cls, attr);
			sb.append(col).append(" ,");

		}
		// 去掉最后一逗号
		sb.setLength(sb.length() - 1);
		sb.append(" from ").append(nameConversion.getTableName(cls)).append(condition);
		return sb.toString();
	}

	/* 参考了AbstractDBStyle的内置代码生成办法 */
	protected String appendIdCondition(SQLManager sm, Class<?> cls) {

		AbstractDBStyle style = (AbstractDBStyle) sm.getDbStyle();
		MetadataManager metadataManager = sm.getMetaDataManager();
		NameConversion nameConversion = sm.getNc();
		String tableName = nameConversion.getTableName(cls);
		StringBuilder condition = new StringBuilder(" where ");
		TableDesc table = metadataManager.getTable(tableName);
		ClassDesc classDesc = table.getClassDesc(cls, nameConversion);

		List<String> colIds = classDesc.getIdCols();
		List<String> propertieIds = classDesc.getIdAttrs();
		Iterator<String> colIt = colIds.iterator();
		Iterator<String> propertieIt = propertieIds.iterator();
		if (colIt.hasNext() && propertieIt.hasNext()) {
			String colId = colIt.next();
			String properId = propertieIt.next();
			condition.append(style.getKeyWordHandler().getCol(colId)).append(" = ").append(style.HOLDER_START)
					.append(properId).append(style.HOLDER_END);
			while (colIt.hasNext() && propertieIt.hasNext()) {
				colId = colIt.next();
				properId = propertieIt.next();
				condition.append(" and ").append(style.getKeyWordHandler().getCol(colId)).append(" = ")
						.append(style.HOLDER_START).append(properId).append(style.HOLDER_END);
			}
		}

		return condition.toString();
	}

}
