package org.beetl.sql.core.db;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.clazz.ClassDesc;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.DefaultKeyWordHandler;
import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.core.*;
import org.beetl.sql.core.concat.*;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.meta.SchemaMetadataManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 按照mysql来的，oralce需要重载insert，page方法
 *
 * @author xiandafu
 */
public abstract class AbstractDBStyle implements DBStyle {

	//翻页从0还是1开始，默认从1开始
	public boolean offsetStartZero = false;
	protected NameConversion nameConversion;
	protected MetadataManager metadataManager;
	protected String lineSeparator = System.getProperty("line.separator", "\n");
	protected KeyWordHandler keyWordHandler = new DefaultKeyWordHandler();
	SQLTemplateEngine sqlTemplateEngine = null;

	public static AssignID DEFAULT_ASSIGNID = null;

	@Data
	public static class MockXXX{
		@AssignID String id;
	}
	static{
		DEFAULT_ASSIGNID = BeanKit.getAnnotation(MockXXX.class,"id",AssignID.class);
	}

	public AbstractDBStyle() {

	}

	@Override
	public void init(SQLTemplateEngine sqlTemplateEngine, Properties ps) {

		this.sqlTemplateEngine = sqlTemplateEngine;
		offsetStartZero = Boolean.parseBoolean(ps.getProperty("OFFSET_START_ZERO", "false").trim());
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
	public SQLSource genSelectById(Class<?> cls, Class viewType) {
		ConcatContext concatContext = this.createConcatContext();
		Select select = concatContext.select();
		appendIdCondition(cls, select);
		select.from(cls);
		if (viewType != null) {
			select.all(cls, viewType);
		} else {
			select.all();
		}
		return new SQLTableSource(select.toSql());
	}

	@Override
	public SQLSource genSelectByIds(Class<?> cls, Class viewType) {
		ConcatContext concatContext = this.createConcatContext();
		Select select = concatContext.select();
		appendJoinInIdsCondition(cls, select);
		select.from(cls);
		if (viewType != null) {
			select.all(cls, viewType);
		} else {
			select.all();
		}
		return new SQLTableSource(select.toSql());
	}


	@Override
	public SQLSource genExistSql(Class<?> cls) {
		ConcatContext concatContext = this.createConcatContext();
		Select select = concatContext.select();
		select.count().from(cls);
		appendIdCondition(cls, select);
		return new SQLTableSource(select.toSql());

	}

	@Override
	public SQLSource genSelectByIdForUpdate(Class<?> cls, Class viewType) {
		SQLSource source = genSelectById(cls, viewType);
		String template = source.getTemplate();
		template = template + " for update";
		source.setTemplate(template);
		return source;
	}

	@Override
	public SQLSource genSelectByTemplate(Class<?> cls, Class viewType) {
		ConcatContext concatContext = this.createConcatContext();
		Select select = concatContext.select();
		if (viewType == null) {
			select.all().from(cls);
		} else {
			select.all(cls, viewType).from(cls);
		}

		getSelectTemplate(cls, select);
		return new SQLTableSource(select.toSql());
	}

	@Override
	public SQLSource genSelectCountByTemplate(Class<?> cls) {
		ConcatContext concatContext = this.createConcatContext();
		Select select = concatContext.select();
		select.count().from(cls);
		getSelectTemplate(cls, select);
		return new SQLTableSource(select.toSql());
	}


	@Override
	public SQLSource genDeleteById(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, this.nameConversion);
		ConcatContext concatContext = this.createConcatContext();

		if (classDesc.getClassAnnotation().getLogicDeleteAttrName() == null) {
			Delete delete = concatContext.delete().from(cls);
			appendIdCondition(cls, delete);
			return new SQLTableSource(delete.toSql());
		} else {
			Update update = concatContext.update().from(cls);
			appendIdCondition(cls, update);
			String col = this.nameConversion.getColName(cls, classDesc.getClassAnnotation().getLogicDeleteAttrName());
			Object value = classDesc.getClassAnnotation().getLogicDeleteAttrValue();
			update.assignConstants(col, value);
			return new SQLTableSource(update.toSql());
		}


	}

	@Override
	public SQLSource genSelectAll(Class<?> cls, Class viewType) {
		Select select = this.createConcatContext().select().from(cls);
		select.from(cls);
		if (viewType == null) {
			select.all();
		} else {
			select.all(cls, viewType);
		}

		String sql = select.toSql();
		return new SQLTableSource(sql);
	}

	@Override
	public SQLSource genUpdateById(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);

		Update update = getUpdate(cls);
		appendIdCondition(cls, update);
		appendVersion(classDesc, update);

		return new SQLTableSource(update.toSql());
	}

	@Override
	public SQLSource genUpdateAbsolute(Class<?> cls) {
		//无条件更新所有，需要谨慎使用，子类可以抛出异常禁止这类方法调用
		Update update = getUpdate(cls);
		return new SQLTableSource(update.toSql());
	}


	@Override
	public SQLSource genUpdateTemplate(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);

		Update update = this.createConcatContext().update().from(cls);
		appendIdCondition(cls, update);
		appendVersion(classDesc, update);


		Iterator<String> cols = classDesc.getInCols().iterator();
		Iterator<String> properties = classDesc.getAttrs().iterator();

		List<String> idCols = classDesc.getIdCols();
		while (cols.hasNext() && properties.hasNext()) {
			String col = cols.next();
			String prop = properties.next();
			if (classDesc.getClassAnnotation().isUpdateIgnore(prop)) {
				continue;
			}
			if (idCols.contains(col)) {
				continue;
			}
			if (prop.equals(classDesc.getClassAnnotation().getVersionProperty())) {
				//版本字段
				update.assignVersion(col);
				continue;
			}
			update.notEmptyAssign(prop, col);

		}
		return new SQLTableSource(update.toSql());
	}

	@Override
	public SQLSource genUpdateAll(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);

		Update update = this.createConcatContext().update().from(cls);

		Iterator<String> cols = classDesc.getInCols().iterator();
		Iterator<String> properties = classDesc.getAttrs().iterator();

		List<String> idCols = classDesc.getIdCols();
		while (cols.hasNext() && properties.hasNext()) {
			String col = cols.next();
			String prop = properties.next();
			if (classDesc.getClassAnnotation().isUpdateIgnore(prop)) {
				continue;
			}
			if (idCols.contains(col)) {
				continue;
			}
			if (prop.equals(classDesc.getClassAnnotation().getVersionProperty())) {
				//版本字段
				update.assignVersion(col);
				continue;
			}
			update.notEmptyAssign(prop, col);

		}
		return new SQLTableSource(update.toSql());
	}

	@Override
	public SQLSource genInsert(Class<?> cls) {
		return generalInsert(cls, false);
	}


	@Override
	public SQLSource genInsertTemplate(Class<?> cls) {
		return generalInsert(cls, true);
	}


	protected SQLSource generalInsert(Class<?> cls, boolean template) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);

		Insert insert = this.createConcatContext().insert().into(cls);


		int idType = DBType.ID_ASSIGN;
		SQLTableSource source = new SQLTableSource();
		Iterator<String> cols = classDesc.getInCols().iterator();
		Iterator<String> attrs = classDesc.getAttrs().iterator();

		List<String> idCols = classDesc.getIdCols();
		while (cols.hasNext() && attrs.hasNext()) {
			String col = cols.next();
			String attr = attrs.next();
			if (classDesc.getClassAnnotation().isInsertIgnore(attr)) {
				continue;
			}

			if (idCols.size() == 1 && idCols.contains(col)) {

				idType = this.getIdType(classDesc.getTargetClass(), attr);
				if (idType == DBType.ID_AUTO) {
					continue; //忽略这个字段
				} else if (idType == DBType.ID_SEQ) {

					SeqID seqId = BeanKit.getAnnotation(classDesc.getTargetClass(), attr,
							(Method) classDesc.getIdMethods().get(attr), SeqID.class);
					insert.setConstant(col, this.getSeqValue(seqId.name()));

					continue;
				} else if (idType == DBType.ID_ASSIGN) {
					//normal
				}
			}

			if (attr.equals(classDesc.getClassAnnotation().getVersionProperty())
					&& classDesc.getClassAnnotation().getInitVersionValue() != -1) {
				//版本字段
				insert.setConstant(col, classDesc.getClassAnnotation().getInitVersionValue() + "");
				continue;

			}

			if (template) {
				insert.conditionalSet(col, attr);
			} else {
				insert.set(col, attr);

			}

		}


		source.setTemplate(insert.toSql());
		source.setIdType(idType);
		source.setTableDesc(table);
		if (idType == DBType.ID_ASSIGN) {
			Map<String, AssignID> map = new HashMap<String, AssignID>();
			for (String idAttr : classDesc.getIdAttrs()) {
				AssignID assignId = BeanKit.getAnnotation(classDesc.getTargetClass(), idAttr, AssignID.class);
				if (assignId != null) {
					map.put(idAttr, assignId);
				}else{
					map.put(idAttr,	DEFAULT_ASSIGNID);
				}
			}
			source.setAssignIds(map);


		}

		return source;
	}


	protected void getSelectTemplate(Class<?> cls, WhereNode wHereNode) {

		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);

		Iterator<String> cols = classDesc.getInCols().iterator();
		Iterator<String> attrs = classDesc.getAttrs().iterator();

		while (cols.hasNext() && attrs.hasNext()) {
			String col = cols.next();
			String attr = attrs.next();
			wHereNode.andIfNotEmpty(col, attr);

		}
	}

	protected Update getUpdate(Class<?> cls) {
		String tableName = nameConversion.getTableName(cls);
		TableDesc table = this.metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);
		Update update = this.createConcatContext().update().from(cls);
		Iterator<String> cols = classDesc.getInCols().iterator();
		Iterator<String> properties = classDesc.getAttrs().iterator();
		List<String> idCols = classDesc.getIdCols();
		while (cols.hasNext() && properties.hasNext()) {
			String col = cols.next();
			String prop = properties.next();

			if (classDesc.getClassAnnotation().isUpdateIgnore(prop)) {
				continue;
			}
			if (idCols.contains(col)) {
				//主键不更新
				continue;
			}
			if (prop.equals(classDesc.getClassAnnotation().getVersionProperty())) {
				//版本字段
				update.assignVersion(col);
				continue;
			}
			update.assign(col).tplValue(prop);

		}
		return update;
	}

	protected void appendVersion(ClassDesc desc, WhereNode node) {
		String property = desc.getClassAnnotation().getVersionProperty();
		if (property == null) {
			return;
		}
		String col = this.nameConversion.getColName(desc.getTargetClass(), property);
		node.andEq(col, property);
	}


	/***
	 * 生成主键条件子句（示例 whrer 1=1 and id=${id}）
	 *
	 * @param cls
	 * @return
	 */
	protected void appendIdCondition(Class<?> cls, WhereNode node) {

		String tableName = nameConversion.getTableName(cls);
		TableDesc table = metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);
		List<String> colIds = classDesc.getIdCols();
		List<String> propertyIds = classDesc.getIdAttrs();
		this.checkId(colIds, propertyIds, cls.getName());
		Iterator<String> colIt = colIds.iterator();
		Iterator<String> propertyIt = propertyIds.iterator();

		if (colIt.hasNext() && propertyIt.hasNext()) {
			String colId = colIt.next();
			String properId = propertyIt.next();
			node.andEq(colId, properId);
			while (colIt.hasNext() && propertyIt.hasNext()) {
				colId = colIt.next();
				properId = propertyIt.next();
				node.andEq(colId, properId);
			}
		}

	}


	protected void appendJoinInIdsCondition(Class<?> cls, WhereNode node) {

		String tableName = nameConversion.getTableName(cls);
		TableDesc table = metadataManager.getTable(tableName);
		ClassDesc classDesc = table.genClassDesc(cls, nameConversion);
		List<String> colIds = classDesc.getIdCols();
		if (colIds.size() > 1) {
			throw new BeetlSQLException(BeetlSQLException.GEN_CODE_ERROR, "不支持联合主键");
		}
		String colId = colIds.get(0);
		node.andIn(colId, "ids");

	}


	protected void checkId(Collection colsId, Collection attrsId, String clsName) {
		if (colsId.size() == 0 || attrsId.size() == 0) {
			throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND,
					"主键未发现," + clsName + ",检查数据库表定义或者NameConversion");
		}
	}

	public String getOrderBy() {
		return lineSeparator + appendExpress("text(has(_orderBy)?' order by '+_orderBy)") + " ";
	}

	public String appendExpress(String express) {
		return sqlTemplateEngine.appendVar(express);
	}

	/**
	 * 根据注解来决定主键采用哪种方式生成。在跨数据库应用中，可以为一个id指定多个注解方式，如mysql，postgres 用auto，oracle 用seq
	 */
	@Override
	public int getIdType(Class c, String idProperty) {
		List<Annotation> ans = BeanKit.getAllAnnotation(c, idProperty);
		//默认是自增长
		int idType = DBType.ID_AUTO;

		for (Annotation an : ans) {
			if (an instanceof AutoID) {
				idType = DBType.ID_AUTO;
				break;
			} else if (an instanceof SeqID) {
				idType = DBType.ID_SEQ;
				break;
			} else if (an instanceof AssignID) {
				idType = DBType.ID_ASSIGN;
				break;
			}
		}

		return idType;

	}

	@Override
	public KeyWordHandler getKeyWordHandler() {
		return this.keyWordHandler;
	}

	@Override
	public void setKeyWordHandler(KeyWordHandler keyWordHandler) {
		this.keyWordHandler = keyWordHandler;
	}

	@Override
	public String getSeqValue(String seqName) {
		throw new UnsupportedOperationException("不支持序列");
	}

	@Override
	public SQLExecutor buildExecutor(ExecuteContext executeContext) {
		SQLExecutor sqlExecutor = null;
		if (preparedStatementSupport()) {
			sqlExecutor = new BaseSQLExecutor(executeContext);
		} else {
			sqlExecutor = new BaseStatementOnlySQLExecutor(executeContext);
		}

		return sqlExecutor;
	}

	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs) {
		metadataManager = new SchemaMetadataManager(cs, this);
		return metadataManager;
	}

	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs, String defaultSchema, String defalutCatalog) {
		metadataManager = new SchemaMetadataManager(cs, defaultSchema, defalutCatalog, this);
		return metadataManager;
	}

	@Override
	public String wrapStatementValue(Object value) {
		if (preparedStatementSupport()) {
			throw new IllegalStateException("支持jdbc PreparedStatement，优先使用PreparedStatement提高性能，保证安全");
		}

		if (value == null) {
			return "null";
		}

		if (value instanceof String) {
			return "'" + value + "'";
		} else if (value instanceof Number) {
			return value.toString();
		} else if (value instanceof java.sql.Timestamp) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String str = sdf.format((Date) value);
			return "parse_datetime('" + str + "',yyyy-MM-dd hh:mm:ss)";
		} else if (value instanceof java.sql.Date) {
			//TODO,确认
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str = sdf.format((Date) value);
			return "parse_date('" + str + "',yyyy-MM-dd)";
		} else if (value instanceof java.util.Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String str = sdf.format((Date) value);
			return "parse_datetime('" + str + "',yyyy-MM-dd hh:mm:ss)";
		} else if (value instanceof Boolean) {

			return ((Boolean) value).toString();
		} else {
			throw new IllegalArgumentException("不支持类型 " + value.getClass() + "," + value);
		}

	}

	protected ConcatContext createConcatContext() {
		ConcatContext concatContext = ConcatContext
				.createTemplateContext(this.nameConversion, this.getKeyWordHandler(), sqlTemplateEngine);
		if (this.keyWordHandler != null) {
			concatContext.setKeyWordHandler(this.keyWordHandler);
		}
		return concatContext;
	}

	public boolean isOffsetStartZero() {
		return offsetStartZero;
	}

	@Override
	public void setOffsetStartZero(boolean offsetStartZero) {
		this.offsetStartZero = offsetStartZero;
	}

	@Override
	public String getDefaultSchema() {
		return null;
	}

	@Override
	public SQLTemplateEngine getSQLTemplateEngine() {
		return this.sqlTemplateEngine;
	}

	@Override
	public void config(SQLManager sqlManager) {

	}

}
