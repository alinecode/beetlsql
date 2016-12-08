package org.beetl.sql.core;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.beetl.sql.ext.gen.JavaType;

/**
 * 
 * @author linziguan@live.com
 *
 */
public class JPAEntityHelper {
	/**
     * 实体对应表的配置信息
     */
    public static class EntityTable {
        private String name;
        private String catalog;
        private String schema;
        //实体类 => 全部列属性
        private Set<EntityColumn> entityClassColumns;
        //实体类 => 主键信息
        private Set<EntityColumn> entityClassPKColumns;
        
        private Map<String, String> colsMap ;
        private Map<String, String> propsMap;
        
        //idColumn
        private EntityColumn idColumn;

        public void setTable(Table table) {
            this.name = table.name();
            this.catalog = table.catalog();
            this.schema = table.schema();
        }

        public String getName() {
            return name;
        }

        public String getCatalog() {
            return catalog;
        }

        public String getSchema() {
            return schema;
        }

        public String getPrefix() {
            if (catalog != null && catalog.length() > 0) {
                return catalog;
            }
            if (schema != null && schema.length() > 0) {
                return catalog;
            }
            return "";
        }

        public Set<EntityColumn> getEntityClassColumns() {
            return entityClassColumns;
        }

        public Set<EntityColumn> getEntityClassPKColumns() {
            return entityClassPKColumns;
        }

        /**
         * @return the idColumn
         */
        public EntityColumn getIdColumn() {
            return idColumn;
        }

        /**
         * @param idColumn the idColumn to set
         */
        public void setIdColumn(EntityColumn idColumn) {
            this.idColumn = idColumn;
        }

		public Map<String, String> getColsMap() {
			return colsMap;
		}

		public Map<String, String> getPropsMap() {
			return propsMap;
		}
    }

    /**
     * 实体字段对应数据库列的信息
     */
    public static class EntityColumn {
        private String property;
        private String column;
        private Class<?> javaType;
        private String jdbcType;
        private String sequenceName;
        private boolean id = false;
        private boolean uuid = false;
        private boolean identity = false;
        private String generator;
        private String orderBy;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public void setJavaType(Class<?> javaType) {
            this.javaType = javaType;
        }

        public String getSequenceName() {
            return sequenceName;
        }

        public void setSequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
        }

        public boolean isId() {
            return id;
        }

        public void setId(boolean id) {
            this.id = id;
        }

        public boolean isUuid() {
            return uuid;
        }

        public void setUuid(boolean uuid) {
            this.uuid = uuid;
        }

        public boolean isIdentity() {
            return identity;
        }

        public void setIdentity(boolean identity) {
            this.identity = identity;
        }

        public String getGenerator() {
            return generator;
        }

        public void setGenerator(String generator) {
            this.generator = generator;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntityColumn that = (EntityColumn) o;

            if (id != that.id) return false;
            if (identity != that.identity) return false;
            if (uuid != that.uuid) return false;
            if (column != null ? !column.equals(that.column) : that.column != null) return false;
            if (generator != null ? !generator.equals(that.generator) : that.generator != null) return false;
            if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) return false;
            if (orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null) return false;
            if (property != null ? !property.equals(that.property) : that.property != null) return false;
            if (sequenceName != null ? !sequenceName.equals(that.sequenceName) : that.sequenceName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = property != null ? property.hashCode() : 0;
            result = 31 * result + (column != null ? column.hashCode() : 0);
            result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
            result = 31 * result + (sequenceName != null ? sequenceName.hashCode() : 0);
            result = 31 * result + (id ? 1 : 0);
            result = 31 * result + (uuid ? 1 : 0);
            result = 31 * result + (identity ? 1 : 0);
            result = 31 * result + (generator != null ? generator.hashCode() : 0);
            result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
            return result;
        }

        /**
         * @return the jdbcType
         */
        public String getJdbcType() {
            return jdbcType;
        }

        /**
         * @param jdbcType the jdbcType to set
         */
        public void setJdbcType(String jdbcType) {
            this.jdbcType = jdbcType;
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
    public static EntityTable getEntityTable(Class<?> entityClass) {
    	EntityTable entityTable=null;
    	synchronized (entityClass) {
    		entityTable= entityTableMap.get(entityClass);
    		if (entityTable == null) {
                initEntityNameMap(entityClass);
                entityTable = entityTableMap.get(entityClass);
            }
		}
    	if (entityTable == null) {
            throw new RuntimeException("无法获取实体类" + entityClass.getCanonicalName() + "对应的表名!");
        }
        return entityTable;
    }

    /**
     * 获取全部列
     *
     * @param entityClass
     * @return
     */
    public static Set<EntityColumn> getColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassColumns();
    }

    /**
     * 获取主键信息
     *
     * @param entityClass
     * @return
     */
    public static Set<EntityColumn> getPKColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassPKColumns();
    }

    /**
     * 获取字段映射关系
     *
     * @param entityClass
     * @return
     */
    public static Map<String, String> getColumnAlias(Class<?> entityClass) {
        Set<EntityColumn> columnList = getColumns(entityClass);
        Map<String, String> alias = new HashMap<String, String>(columnList.size());
        for (EntityColumn column : columnList) {
            alias.put(column.getColumn(), column.getProperty());
        }
        return alias;
    }

    /**
     * 获取查询的Select
     *
     * @param entityClass
     * @return
     */
    public static String getSelectColumns(Class<?> entityClass) {
        Set<EntityColumn> columnList = getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        boolean skipAlias = Map.class.isAssignableFrom(entityClass);
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(entityColumn.getColumn());
            if (!skipAlias && !entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty())) {
                selectBuilder.append(" ").append(entityColumn.getProperty().toUpperCase()).append(",");
            } else {
                selectBuilder.append(",");
            }
        }
        return selectBuilder.substring(0, selectBuilder.length() - 1);
    }

    /**
     * 获取查询的Select
     *
     * @param entityClass
     * @return
     */
    public static String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnList = getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(entityColumn.getColumn()).append(",");
        }
        return selectBuilder.substring(0, selectBuilder.length() - 1);
    }

    /**
     * 获取主键的Where语句
     *
     * @param entityClass
     * @return
     */
    public static String getPrimaryKeyWhere(Class<?> entityClass) {
        Set<JPAEntityHelper.EntityColumn> entityColumns = JPAEntityHelper.getPKColumns(entityClass);
        StringBuilder whereBuilder = new StringBuilder();
        for (JPAEntityHelper.EntityColumn column : entityColumns) {
            whereBuilder.append(column.getColumn()).append(" = ?").append(" AND ");
        }
        return whereBuilder.substring(0, whereBuilder.length() - 4);
    }

    /**
     * 初始化实体属性
     *
     * @param entityClass
     */
    public static synchronized void initEntityNameMap(Class<?> entityClass) {
        if (entityTableMap.get(entityClass) != null) {
            return;
        }
        //表名
        EntityTable entityTable = null;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if (!table.name().equals("")) {
                entityTable = new EntityTable();
                entityTable.setTable(table);
            }
        }
        //列
        List<Field> fieldList = getAllField(entityClass, null);
        Set<EntityColumn> columnSet = new HashSet<EntityColumn>();
        Set<EntityColumn> pkColumnSet = new HashSet<EntityColumn>();
        Map<String, String> cols = new CaseInsensitiveMap();
        Map<String, String> props= new CaseInsensitiveMap();
        
        EntityColumn perIdEntityColumn=null;
        for (Field field : fieldList) {
            try {
                //排除字段
                if (field.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                EntityColumn entityColumn = new EntityColumn();
                PropertyDescriptor p = new PropertyDescriptor(field.getName(), entityClass);
                Method method=p.getReadMethod();
                if (field.isAnnotationPresent(Id.class) ||method.isAnnotationPresent(Id.class)) {
                    entityColumn.setId(true);
                    entityTable.setIdColumn(entityColumn);
                }else if(field.getName().toUpperCase().equals("ID")){
                	perIdEntityColumn=entityColumn;
                }
                String columnName = null;
                String jdbcType=null;
                
                Column column=null;
                if (field.isAnnotationPresent(Column.class)) {
                    column = field.getAnnotation(Column.class);
                }else if(method.isAnnotationPresent(Column.class)){
                    column=method.getAnnotation(Column.class);
                }
                
                if(column!=null){
                    columnName = column.name();
                    jdbcType = column.columnDefinition();
                }
                if (StringUtils.isBlank(columnName)) {
                    columnName = field.getName();
                }
                
                if(StringUtils.isBlank(jdbcType)){
                    jdbcType=getPropertyJdbcType(p);
                }
                
                
                entityColumn.setProperty(field.getName());
                entityColumn.setColumn(columnName.toUpperCase());
                entityColumn.setJavaType(field.getType());
                entityColumn.setJdbcType(jdbcType);
                //order by
                if (field.isAnnotationPresent(OrderBy.class)) {
                    OrderBy orderBy = field.getAnnotation(OrderBy.class);
                    if (orderBy.value().equals("")) {
                        entityColumn.setOrderBy("ASC");
                    } else {
                        entityColumn.setOrderBy(orderBy.value());
                    }
                }
                //主键策略 - Oracle序列，MySql自动增长，UUID
                if (field.isAnnotationPresent(SequenceGenerator.class)) {
                    SequenceGenerator sequenceGenerator = field.getAnnotation(SequenceGenerator.class);
                    if (sequenceGenerator.sequenceName().equals("")) {
                        throw new RuntimeException(entityClass + "字段" + field.getName() + "的注解@SequenceGenerator未指定sequenceName!");
                    }
                    entityColumn.setSequenceName(sequenceGenerator.sequenceName());
                } else if (field.isAnnotationPresent(GeneratedValue.class)) {
                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                    if (generatedValue.generator().equals("UUID")) {
                        if (field.getType().equals(String.class)) {
                            entityColumn.setUuid(true);
                        } else {
                            throw new RuntimeException(field.getName() + " - 该字段@GeneratedValue配置为UUID，但该字段类型不是String");
                        }
                    } else if (generatedValue.generator().equals("JDBC")) {
                        if (Number.class.isAssignableFrom(field.getType())) {
                            entityColumn.setIdentity(true);
                            entityColumn.setGenerator("JDBC");
                        } else {
                            throw new RuntimeException(field.getName() + " - 该字段@GeneratedValue配置为UUID，但该字段类型不是String");
                        }
                    }
                }
                columnSet.add(entityColumn);
                if (entityColumn.isId()) {
                    pkColumnSet.add(entityColumn);
                }
                cols.put(entityColumn.getProperty(), entityColumn.getColumn());
                props.put(entityColumn.getColumn(), entityColumn.getProperty());
            } catch (IntrospectionException ex) {
                Logger.getLogger(JPAEntityHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(entityTable.getIdColumn()==null){
        	perIdEntityColumn.setId(true);
        	entityTable.setIdColumn(perIdEntityColumn);
        }
        entityTable.colsMap=cols;
        entityTable.propsMap=props;
        entityTable.entityClassColumns = columnSet;
        if (pkColumnSet.size() == 0) {
            entityTable.entityClassPKColumns = columnSet;
        } else {
            entityTable.entityClassPKColumns = pkColumnSet;
        }
        //缓存
        entityTableMap.put(entityClass, entityTable);
    }
    
    public static String getPropertyJdbcType(PropertyDescriptor p) {
        String methodName = null;
        Method method = p.getReadMethod();
        if (method.isAnnotationPresent(Column.class)) {
            Annotation columnAnnotation = method.getAnnotation(Column.class);
            if (null != columnAnnotation) {
                Column column = (Column) columnAnnotation;
                String columnDefine = column.columnDefinition();
                if (StringUtils.isNotBlank(columnDefine)) {
                    methodName = columnDefine;
                }
            }

        }
        if (StringUtils.isBlank(methodName)) {
            methodName = JavaType.javaTypeToJdbcType(p.getPropertyType());
        }
        return methodName;
    }

    public static void main(String[] args) {
        System.out.println(camelhumpToUnderline("userName"));
        System.out.println(camelhumpToUnderline("userPassWord"));
        System.out.println(camelhumpToUnderline("ISO9001"));
        System.out.println(camelhumpToUnderline("hello_world"));
    }

    /**
     * 将驼峰风格替换为下划线风格
     */
    public static String camelhumpToUnderline(String str) {
        final int size;
        final char[] chars;
        final StringBuilder sb = new StringBuilder(
                (size = (chars = str.toCharArray()).length) * 3 / 2 + 1);
        char c;
        for (int i = 0; i < size; i++) {
            c = chars[i];
            if (isUppercaseAlpha(c)) {
                sb.append('_').append(c);
            } else {
                sb.append(toUpperAscii(c));
            }
        }
        return sb.charAt(0) == '_' ? sb.substring(1) : sb.toString();
    }

    /**
     * 将下划线风格替换为驼峰风格
     */
    public static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    public static boolean isUppercaseAlpha(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public static char toUpperAscii(char c) {
        if (isUppercaseAlpha(c)) {
            c -= (char) 0x20;
        }
        return c;
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
            //排除静态字段，解决bug#2
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldList.add(field);
            }
        }
        Class<?> superClass = entityClass.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && (superClass.isAnnotationPresent(Entity.class)
                || (!Map.class.isAssignableFrom(superClass)
                && !Collection.class.isAssignableFrom(superClass)))) {
            return getAllField(entityClass.getSuperclass(), fieldList);
        }
        return fieldList;
    }
}
