package org.beetl.sql.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.beetl.sql.core.kit.StringKit;

/**
 * 
 * @author linziguan@live.com、darren
 *
 */
public class JPAEntityHelper {
	/**
	 * 实体对应表的配置信息
	 */
	public static class EntityTable {
		// 实体映射的数据库表名
		private String name;
		// Java属性映射的数据库字段集合（column -> property）
		private Map<String, String> colsMap = new HashMap<String, String>();
		// 数据库字段映射的Java属性集合（property -> column）
		private Map<String, String> propsMap = new HashMap<String, String>();

		protected void setTable(Table table) {
			name = table.name();
			if (StringKit.isNotBlank(table.schema())) {
				name = table.schema() + "." + name;
			} else if (StringKit.isNotBlank(table.catalog())) {
				name = table.catalog() + "." + name;
			}
		}
		protected void addProp(String col, String prop) {
			propsMap.put(col, prop);
		}
		protected void addCol(String prop, String col) {
			colsMap.put(prop, col);
		}
		protected void setName(String name) {
			this.name = name;
		}

		/**
		 * 根据column获取property
		 * 
		 * @param col
		 *            数据库列名
		 * @return
		 */
		public String getProp(String col) {
			return propsMap.get(col);
		}

		/**
		 * 根据property获取column
		 * 
		 * @param prop
		 *            Java属性
		 * @return
		 */
		public String getCol(String prop) {
			return colsMap.get(prop);
		}

		/**
		 * 获取数据库表名
		 * 
		 * @return
		 */
		public String getName() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (name == null ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			EntityTable other = (EntityTable) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}
	}

	/**
	 * 实体类 => 表对象
	 */
	private static final Map<Class<?>, EntityTable> entityTableMap = new HashMap<Class<?>, EntityTable>();

	/**
	 * 获取表对象
	 *
	 * @param entityClass
	 * @return
	 */
	public static EntityTable getEntityTable(Class<?> entityClass,NameConversion nc) {
		EntityTable entityTable = null;
		synchronized (entityClass) {
			entityTable = entityTableMap.get(entityClass);
			if (entityTable == null) {
				initEntityNameMap(entityClass,nc);
				entityTable = entityTableMap.get(entityClass);
			}
		}
		if (entityTable == null) {
			throw new RuntimeException("无法获取实体类" + entityClass.getCanonicalName() + "对应的表名!");
		}
		return entityTable;
	}

	/**
	 * 初始化实体属性
	 *
	 * @param entityClass
	 */
	private static synchronized void initEntityNameMap(Class<?> entityClass,NameConversion nc) {
		if (entityTableMap.get(entityClass) != null) {
			return;
		}
		EntityTable entityTable = new EntityTable();
		if (entityClass.isAnnotationPresent(Table.class)) {
			Table table = entityClass.getAnnotation(Table.class);
			if (StringKit.isNotBlank(table.name())) {
				entityTable.setTable(table);
			}
		}else if(entityClass.isAnnotationPresent(org.beetl.sql.core.annotatoin.Table.class)){//当用户错误注解成了BeetlSQL下面的@Table时兼容处理
			org.beetl.sql.core.annotatoin.Table table = entityClass.getAnnotation(org.beetl.sql.core.annotatoin.Table.class);
			entityTable.setName(table.name());
		}
		if(StringKit.isBlank(entityTable.getName())){//当从@Table没有获取到name时采用默认NameConversion转换或直接使用Class类名
			entityTable.setName(nc!=null?nc.getTableName(entityClass):entityClass.getSimpleName());
		}
		// 列
		List<Field> fieldList = getAllField(entityClass, null);
		for (Field field : fieldList) {
			String propName = field.getName();
			Method method = null;
			try {
					//符合JavaBean规范的get方法名称（userName=>getUserName,uName=>getuName）
					method = entityClass.getMethod("get"+(propName.length()>1&&propName.charAt(1)>='A'&&propName.charAt(1)<='Z'?propName:StringKit.toUpperCaseFirstOne(propName)));
			} catch (Exception e) {
//				Logger.getLogger(JPAEntityHelper.class.getName()).log(Level.WARNING, null, e.getMessage());
			}
			Column column = null;
			if (field.isAnnotationPresent(Column.class)) {
				column = field.getAnnotation(Column.class);
			} else if (method!=null&&method.isAnnotationPresent(Column.class)) {
				column = method.getAnnotation(Column.class);
			}
			String columnName = null;
			if (column != null){
				columnName = column.name();
			}
			if(StringKit.isBlank(columnName)){//当没有从JPA@Column获取到列名时采用默认的NameConversion或直接使用属性名
				columnName = nc!=null?nc.getColName(propName):propName;
			}
			// @Transient 排除字段 强制将映射返回null，让BeetlSQL自己跳过
			if(field.isAnnotationPresent(Transient.class)||method!=null&&method.isAnnotationPresent(Transient.class)){
				entityTable.addCol(propName, null);
				entityTable.addProp(columnName, null);
			}else{
				entityTable.addCol(propName, columnName);
				entityTable.addProp(columnName, propName);
			}
		}
		// 缓存
		entityTableMap.put(entityClass, entityTable);
	}

	/**
	 * 获取全部的Field
	 *
	 * @param entityClass
	 * @param fieldList
	 * @return
	 */
	private static List<Field> getAllField(Class<?> entityClass, List<Field> fieldList) {
		if (fieldList == null) {
			fieldList = new ArrayList<Field>();
		}
		if (entityClass.equals(Object.class)) {
			return fieldList;
		}
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			// 排除静态字段，解决bug#2
			if (!Modifier.isStatic(field.getModifiers())) {
				fieldList.add(field);
			}
		}
		Class<?> superClass = entityClass.getSuperclass();
		if (superClass != null && !superClass.equals(Object.class) &&!Map.class.isAssignableFrom(superClass) && !Collection.class.isAssignableFrom(superClass)) {
			return getAllField(entityClass.getSuperclass(), fieldList);
		}
		return fieldList;
	}
}
