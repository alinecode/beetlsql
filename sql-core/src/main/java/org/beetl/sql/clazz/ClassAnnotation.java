package org.beetl.sql.clazz;


import lombok.Data;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.builder.BeanConvert;
import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.annotation.builder.TargetAdditional;
import org.beetl.sql.annotation.entity.*;
import org.beetl.sql.clazz.kit.*;
import org.beetl.sql.core.mapping.BeanFetch;
import org.beetl.sql.core.mapping.ResultSetMapper;
import org.beetl.sql.core.mapping.RowMapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;


/**
 * BeetlSQL重要类，记录了实体类与所有字段的注解,用于BeetlSQL个性化功能的配置
 *
 * @author xiandafu
 * @since 3.0
 */
@Data
public class ClassAnnotation {

	static DefaultCache<Class, ClassAnnotation> cache = new DefaultCache<>();

	//实体对象
	Class entityClass = null;
	//@Table 对应的名字
	String tableName = null;
	//使用@Column注解后，属性到列明的映射
	Map<String, String> attrAnnotationName = new HashMap<>();
	//使用@Column注解后 列名到属性映射
	CaseInsensitiveHashMap<String, String> colAnnotationName = new CaseInsensitiveHashMap<>();

	//update和insert 忽略策略
	Set<String> attrUpdateIgnores = null;
	Set<String> attrInsertIgnores = null;


	// 逻辑删除标记以及设置的默认值
	String logicDeleteAttrName = null;
	int logicDeleteAttrValue = 0;

	//版本号标记和设置的初始值
	String versionProperty;
	int initVersionValue = -1;
	/**
	 *  额外的映射方式，rowMapper，resultSetMapper
	 *  这是因为他们都是用了ResultSet作为遍历
	 */
	RowMapper rowMapper = null;
	ResultSetMapper resultSetMapper = null;
	/**
	 * 被{@link ProviderConfig} 注解的注解
	 */
	Annotation mapperConfig = null;
	BeanFetch beanFetch = null;
	Annotation beanFetchAnnotation = null;

	//	Map<String,Annotation> fetechMap;

	/**
	 * 插入实体时候自动从数据库获取的值，参考{@link Auto}
	 */
	Set<String> autoAttrList = null;

	/**
	 *  {@link AutoID}
	 */
	String autoIdAttr = null;


	Set<String> seqAttrList = null;
	/**
	 * {@link SeqID}
	 */
	String seqIdAttr = null;

	/**
	 * 综合了AutoId和Auto,Seq,SeqId
	 */
	transient  String[] keyHolderArray = null;
	/**
	 * 用户自定义的注解扩展
	 */
	ClassExtAnnotation extAnnotation = new ClassExtAnnotation();
	/**
	 * 用户是否有自定义的扩展注解
	 */
	boolean containExtAnnotation = false;


	protected ClassAnnotation(Class entityClass) {
		this.entityClass = entityClass;
	}

	public static ClassAnnotation getClassAnnotation(Class entity) {
		ClassAnnotation ca = cache.get(entity);
		if (ca != null) {
			return ca;
		}

		ca = new ClassAnnotation(entity);
		ca.init();
		cache.put(entity, ca);
		return ca;
	}

	protected void init() {
		if (BeanKit.isJavaClass(entityClass)) {
			return;
		}
		/*检查映射相关注解*/
		mappingCheck();
		/*检查属性相关注解*/
		propertyCheck();
		/*检查可扩展注解*/
		checkExtAnnotation();

	}


	/**
	 * 检查额外的用户自定义注解
	 */
	protected void checkExtAnnotation() {
		Annotation[] ans = this.entityClass.getAnnotations();
		for (Annotation an : ans) {
			Builder builder = an.annotationType().getAnnotation(Builder.class);
			if (builder == null) {
				continue;
			}
			Class ext = builder.value();
			if (TargetAdditional.class.isAssignableFrom(ext)) {
				extAnnotation.setAdditionalAnnotation(an);
				extAnnotation.setTargetAdditional(BeanKit.<TargetAdditional>newSingleInstance(ext));
			} else if (BeanConvert.class.isAssignableFrom(ext)) {
				extAnnotation.setBeanConvert(BeanKit.<BeanConvert>newSingleInstance(ext));
				extAnnotation.setBeanConvertAnnotation(an);
			} else if (BeanFetch.class.isAssignableFrom(ext)) {
				this.beanFetch = BeanKit.<BeanFetch>newSingleInstance(ext);
				this.beanFetchAnnotation = an;
			} else {
				//忽略不关心的
			}

		}

		PropertyDescriptor[] ps = this.getPropertyDescriptor(this.entityClass);
		for (PropertyDescriptor p : ps) {
			String attr = p.getName();
			List<Annotation> attrAns = BeanKit.getAllAnnotation(entityClass, attr);
			for (Annotation an : attrAns) {
				Class attrExt = getBuilderAnnotation(an);
				if (attrExt == null) {
					continue;
				}
				if (AttributeConvert.class.isAssignableFrom(attrExt)) {
					extAnnotation.addAttributeConvert(attr, BeanKit.<AttributeConvert>newSingleInstance(attrExt));
				}
			}
		}
		containExtAnnotation = extAnnotation.hasAttributeExt() || extAnnotation.hasEntityExt();
	}

	/**
	 * 该实体自定义的映射方式
	 * @see ResultProvider
	 * @see RowProvider
	 */
	protected void mappingCheck() {
		/*映射表的注解*/
		Table table = BeanKit.getAnnotation(entityClass, Table.class);
		if (table != null) {
			this.tableName = table.name();
		}

		ResultProvider mappingConfig = (ResultProvider) this.entityClass.getAnnotation(ResultProvider.class);
		if (mappingConfig != null) {
			this.resultSetMapper = BeanKit.newSingleInstance(mappingConfig.value());
		}

		RowProvider provider = (RowProvider) this.entityClass.getAnnotation(RowProvider.class);
		if (provider != null) {
			this.rowMapper = BeanKit.newInstance(provider.value());
		}
		if (rowMapper != null && resultSetMapper != null) {
			throw new IllegalArgumentException("rowMapper 或者 resultSetMapper 不能同时存在于" + entityClass);
		}

		Annotation[] ans = this.entityClass.getAnnotations();
		for (Annotation an : ans) {
			ProviderConfig builder = an.annotationType().getAnnotation(ProviderConfig.class);
			if (builder != null) {
				this.mapperConfig = an;
			}
		}

	}


	protected void propertyCheck() {
		PropertyDescriptor[] ps = this.getPropertyDescriptor(this.entityClass);
		for (PropertyDescriptor p : ps) {
			Method readMethod = p.getReadMethod();
			Class type = p.getPropertyType();
			String attr = p.getName();
			if (type.isEnum()) {
				// 判断实体类上是否有枚举映射注解，用于结果集映射
				EnumMapping enumMapping = BeanKit.getAnnotation(entityClass, attr, readMethod, EnumMapping.class);
				if (enumMapping != null) {
					String enumAttr = enumMapping.value();
					EnumKit.init(type, enumAttr);
				} else {
					EnumKit.init(type);
				}
			}
			// 字段与数据列映射注解
			Column column = BeanKit.getAnnotation(entityClass, attr, readMethod, Column.class);
			if (column != null) {
				String col = column.value();
				this.attrAnnotationName.put(attr, col);
				this.colAnnotationName.put(col, attr);
			}

			Auto auto = BeanKit.getAnnotation(entityClass, attr, readMethod, Auto.class);
			if (auto != null) {
				checkAutoAttrList();
				autoAttrList.add(attr);
			}

			AutoID autoId = BeanKit.getAnnotation(entityClass, attr, readMethod, AutoID.class);
			if (autoId != null) {
				if(this.autoIdAttr!=null){
					throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,"@AutoId 应该只有一个，但出现在"
							+this.autoIdAttr+" 和 "+attr+" @"+this.entityClass );
				}
				this.autoIdAttr = attr;
			}


			SeqID seqId = BeanKit.getAnnotation(entityClass, attr, readMethod, SeqID.class);
			if (seqId != null) {
				if(this.seqIdAttr!=null){
					throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR,"@SeqId 应该只有一个，但出现在"
							+this.autoIdAttr+" 和 "+attr+" @"+this.entityClass );
				}
				this.seqIdAttr = attr;

			}

			Seq seq = BeanKit.getAnnotation(entityClass, attr, readMethod, Seq.class);
			if (seq != null) {
				checkSeqAttrList();
				seqAttrList.add(attr);
			}

			InsertIgnore ig = BeanKit.getAnnotation(entityClass, attr, readMethod, InsertIgnore.class);
			if (ig != null) {
				checkAttrInsertIgnores();
				this.attrInsertIgnores.add(attr);
			}

			UpdateIgnore ug = BeanKit.getAnnotation(entityClass, attr, readMethod, UpdateIgnore.class);
			if (ug != null) {
				checkAttrUpdateIgnores();
				this.attrUpdateIgnores.add(attr);
			}

			LogicDelete logicDelete = BeanKit.getAnnotation(entityClass, attr, readMethod, LogicDelete.class);
			if (logicDelete != null) {
				this.logicDeleteAttrName = p.getName();
				this.logicDeleteAttrValue = logicDelete.value();
			}

			Version version = BeanKit.getAnnotation(entityClass, attr, readMethod, Version.class);
			if (version != null) {
				this.versionProperty = attr;
				this.initVersionValue = version.value();
			}
		}


		makeKeyHolder();

	}

	protected  void makeKeyHolder(){

		Set<String> keyHolderList = new HashSet();
		if(autoAttrList!=null){
			keyHolderList.addAll(autoAttrList);
		}
		if(this.autoIdAttr!=null){
			keyHolderList.add(autoIdAttr);
		}

		if(seqAttrList!=null){
			keyHolderList.addAll(seqAttrList);
		}

		if(seqIdAttr!=null){
			keyHolderList.add(seqIdAttr);
		}

		keyHolderArray = keyHolderList.toArray(new String[0]);
	}


	/**
	 * 查找annotation
	 * @param userCustomizedAnnotation
	 * @return
	 */
	protected Class getBuilderAnnotation(Annotation userCustomizedAnnotation) {
		Builder builder = userCustomizedAnnotation.annotationType().getAnnotation(Builder.class);
		if (builder == null) {
			return null;
		}
		return builder.value();
	}


	public PropertyDescriptor[] getPropertyDescriptor(Class entityClass) {
		try {
			return BeanKit.propertyDescriptors(entityClass);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}


	public boolean isInsertIgnore(String attrName) {

		return attrInsertIgnores != null && attrInsertIgnores.contains(attrName);
	}

	public boolean isUpdateIgnore(String attrName) {
		return attrUpdateIgnores != null && attrUpdateIgnores.contains(attrName);
	}

	public BeanFetch getBeanFetch() {
		return beanFetch;
	}


	public String[] getInsertAutoAttrs() {
		return this.keyHolderArray;
	}


	protected void checkAutoAttrList() {
		if (autoAttrList == null) {
			autoAttrList = new CaseInsensitiveOrderSet();
		}
	}

	protected void checkSeqAttrList() {
		if (this.seqAttrList == null) {
			seqAttrList = new CaseInsensitiveOrderSet();
		}
	}

	protected void checkAttrInsertIgnores() {
		if (attrInsertIgnores == null) {
			attrInsertIgnores = new HashSet();
		}

	}

	protected void checkAttrUpdateIgnores() {
		if (attrUpdateIgnores == null) {
			attrUpdateIgnores = new HashSet<>();
		}
	}

	public boolean isAutoAttr(String attr){
		return autoAttrList!=null&&autoAttrList.contains(attr);
	}

	public boolean isSeqAttr(String attr){
		return seqAttrList!=null&&seqAttrList.contains(attr);
	}
}
