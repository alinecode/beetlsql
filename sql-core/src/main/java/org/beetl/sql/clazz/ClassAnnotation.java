package org.beetl.sql.clazz;


import lombok.Data;
import org.beetl.sql.annotation.builder.TargetAdditional;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.builder.BeanConvert;
import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.annotation.entity.*;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.clazz.kit.DefaultCache;
import org.beetl.sql.clazz.kit.EnumKit;
import org.beetl.sql.core.mapping.BeanFetch;
import org.beetl.sql.core.mapping.ResultSetMapper;
import org.beetl.sql.core.mapping.RowMapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


/**
 * BeetlSQL重要类，记录了class及其属性的所有注解,用于各种骚操作的配置
 *
 * 一个bean可以有多个注解。
 * @author xiandafu
 * @since  3.0
 */
@Data
public class ClassAnnotation {

    static DefaultCache<Class,ClassAnnotation> cache = new DefaultCache<Class,ClassAnnotation>();

    //实体对象
    Class entityClass = null;
    //@Table 对应的名字
    String tableName = null;
	//使用@Column注解后，属性到列明的映射
    Map<String,String> attrAnnotationName = new HashMap<>();
    //使用@Column注解后 列名到属性映射
	CaseInsensitiveHashMap<String,String> colAnnotationName = new CaseInsensitiveHashMap<>();

    //update和insert 忽略策略
    Set<String> attrUpdateIgnores = null;
	Set<String> attrInsertIgnores = null;
	/**
	 * 枚举映射,记录一下，实际完成在{@code org.beetl.sql.core.kit.EnumKit}
	 * @see EnumValue
	 * @see EnumMapping
	 */
	Map<String,String> enums = null;

    // 逻辑删除标记以及设置的默认值
    String logicDeleteAttrName =null;
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
	Annotation  mapperConfig = null;


	BeanFetch beanFetch = null;
	Annotation beanFetchAnnotation = null;

//
//	Map<String,Annotation> fetechMap;

	/**
	 * 插入实体时候自动从数据库获取的值，参考{@link Auto} 和 {@link AutoID} {@link SeqID}
	 */
	List<String> autoAttrList = null;
	/**
	 * 用户自定义的注解扩展
	 */
	ClassExtAnnoation extAnnotation = new ClassExtAnnoation() ;
	/**
	 * 用户是否有自定义的扩展注解
	 */
	boolean containExtAnnotation = false;


    public static ClassAnnotation getClassAnnotation(Class entity){
        ClassAnnotation ca = cache.get(entity);
        if(ca!=null){
            return ca;
        }


        ca = new ClassAnnotation(entity);
        ca.init();
        cache.put(entity,ca);
        return ca;
    }

    protected  ClassAnnotation(Class entityClass){
        this.entityClass = entityClass;


    }

    protected void init(){
		if(BeanKit.isJavaClass(entityClass)){
			return ;
		}
    	mappingCheck();
        propertyCheck();
        checkExtAnnotation();

    }




	/**
	 * 检查额外的用户自定义注解
	 */
	protected  void checkExtAnnotation(){
		Annotation[] ans = this.entityClass.getAnnotations();
		for(Annotation an:ans){
			Builder builder = an.annotationType().getAnnotation(Builder.class);
			if(builder==null){
				continue;
			}
			Class ext = builder.value();
			if(TargetAdditional.class.isAssignableFrom(ext)){
				extAnnotation.setAdditionalAnnotation(an);
				extAnnotation.setTargetAdditional((TargetAdditional)BeanKit.newSingleInstance(ext));
			}else if(BeanConvert.class.isAssignableFrom(ext)){
				extAnnotation.setBeanConvert((BeanConvert)BeanKit.newSingleInstance(ext));
				extAnnotation.setBeanConvertAnnotation(an);
			}else if(BeanFetch.class.isAssignableFrom(ext)){
				this.beanFetch = (BeanFetch)BeanKit.newSingleInstance(ext);
				this.beanFetchAnnotation = an;
			}
			else{
				//忽略不关心的
			}

		}

		PropertyDescriptor[] ps = this.getPropertyDescriptor(this.entityClass);
		for(PropertyDescriptor p:ps){
			String attr = p.getName();
			List<Annotation> attrAns = BeanKit.getAllAnnotation(entityClass,attr);
			for(Annotation an:attrAns) {
				Class attrExt = getBuilderAnnotation(an);
				if(attrExt==null){
					continue;
				}
				if (AttributeConvert.class.isAssignableFrom(attrExt)) {
					extAnnotation.addAttributeConvert(attr,(AttributeConvert) BeanKit.newSingleInstance(attrExt));
				}
			}
		}

		containExtAnnotation = extAnnotation.hasAttributeExt()|| extAnnotation.hasEntityExt();

	}

	/**
	 * 该实体自定义的映射方式
	 * @see ResultProvider
	 * @see RowProvider
	 */
    protected void mappingCheck(){

		Table table = (Table)BeanKit.getAnnotation(entityClass,Table.class);;

		if(table!=null){
			this.tableName = table.name();
		}
		ResultProvider mappingConfig = (ResultProvider)this.entityClass.getAnnotation(
				ResultProvider.class);
		if(mappingConfig!=null){
			this.resultSetMapper = (ResultSetMapper)BeanKit.newSingleInstance(mappingConfig.value());
		}
		RowProvider provider = (RowProvider)this.entityClass.getAnnotation(RowProvider.class);
		if(provider!=null){
			this.rowMapper = (RowMapper) BeanKit.newInstance(provider.value());
		}



		if(rowMapper!=null&&resultSetMapper!=null){
			throw new IllegalArgumentException("rowMapper 或者 resultSetMapper 不能同时存在于"+entityClass);
		}

		Annotation[] ans = this.entityClass.getAnnotations();
		for(Annotation an:ans){
			ProviderConfig builder = an.annotationType().getAnnotation(ProviderConfig.class);
			if(builder!=null){
				this.mapperConfig = an;
			}
		}

    }



    protected  void propertyCheck(){
        PropertyDescriptor[] ps = this.getPropertyDescriptor(this.entityClass);
        for(PropertyDescriptor p:ps){
            Method readMethod =  p.getReadMethod();
            Class type = p.getPropertyType();
			String attr = p.getName();
			//各种内置注解
            if(type.isEnum()){
				EnumMapping enumMapping = BeanKit.getAnnotation(entityClass, attr, readMethod, EnumMapping.class);
				if(enumMapping!=null){
					String enumAttr = enumMapping.value();
					if(this.enums==null){
						this.enums = new HashMap<>();
					}
					this.enums.put(attr,enumAttr);
					EnumKit.init(type,enumAttr);
				}else{
					String enumAttr = lookupEnumValueAttr(type);
					if(enumAttr!=null){
						EnumKit.init(type,enumAttr);
					}else{
						EnumKit.initNoAnotation(type);
					}
				}
            }

			Column column = BeanKit.getAnnotation(entityClass, attr, readMethod, Column.class);
			if(column!=null){
				String col = column.value();
				this.attrAnnotationName.put(attr,col);
				this.colAnnotationName.put(col,attr);
			}


			Auto auto = BeanKit.getAnnotation(entityClass, attr, readMethod, Auto.class);
			if(auto!=null){
				checkAutoAttrList();
				autoAttrList.add(attr);
			}

			AutoID  autoId = BeanKit.getAnnotation(entityClass, attr, readMethod, AutoID.class);
			if(autoId!=null){
				checkAutoAttrList();
				autoAttrList.add(0,attr);
			}

			SeqID seqId = BeanKit.getAnnotation(entityClass, attr, readMethod, SeqID.class);
			if(seqId!=null){
				checkAutoAttrList();
				autoAttrList.add(0,attr);
			}


			InsertIgnore ig = BeanKit.getAnnotation(entityClass, attr, readMethod, InsertIgnore.class);
			UpdateIgnore ug = BeanKit.getAnnotation(entityClass, attr, readMethod, UpdateIgnore.class);
			if(ig!=null){
				checkAttrInsertIgnores();
				this.attrInsertIgnores.add(attr);
			}

			if(ug!=null){
				checkAttrUpdateIgnores();
				this.attrUpdateIgnores.add(attr);
			}

            LogicDelete logicDelete =  BeanKit.getAnnotation(entityClass, attr, readMethod, LogicDelete.class);
            if(logicDelete!=null) {
                this.logicDeleteAttrName = p.getName();
                this.logicDeleteAttrValue =logicDelete.value();
            }

            Version version =  BeanKit.getAnnotation(entityClass, attr, readMethod, Version.class);
            if(version!=null){
                this.versionProperty = attr;
                this.initVersionValue =version.value();
            }

        }

    }



    private String lookupEnumValueAttr(Class enumClass){
		PropertyDescriptor[] ps = getPropertyDescriptor(enumClass);
		for(PropertyDescriptor p:ps){
			Method readMethod =  p.getReadMethod();
			if(readMethod.getDeclaringClass()==Object.class){
				continue;
			}
			Class type = p.getPropertyType();
			String attr = p.getName();
			EnumValue enumValue = BeanKit.getAnnotation(enumClass, attr, readMethod, EnumValue.class);
			if(enumValue!=null){
				return attr;
			}
		}

		return null;

	}

	/**
	 * 查找annoation
	 * @param userCustomizedAnnatation
	 * @return
	 */
	protected Class getBuilderAnnotation(Annotation userCustomizedAnnatation){
		Builder builder  = userCustomizedAnnatation.annotationType().getAnnotation(Builder.class);
		if(builder==null){
			return null;
		}
		return builder.value();
	}



    public PropertyDescriptor[] getPropertyDescriptor(Class entityClass){
        try {
            return BeanKit.propertyDescriptors(entityClass);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }


	public boolean isInsertIgnore(String attrName){

		return attrInsertIgnores!=null&&attrInsertIgnores.contains(attrName);
	}

	public boolean isUpdateIgnore(String attrName){
		return attrUpdateIgnores!=null&&attrUpdateIgnores.contains(attrName);
	}

	public BeanFetch getBeanFetch() {
		return beanFetch;
	}


	public String[] getInsertAutoAttrs(){
		if(autoAttrList==null){
			return  BeanKit.EMP_STRING_ARRAY;
		}
		return autoAttrList.toArray(new String[0]);
	}

	public String[] getInsertAutoCols(NameConversion nc) {
		if(this.autoAttrList.isEmpty()){
			return null ;
		}
		return autoAttrList.stream()
				.map(t ->  nc.getColName(this.entityClass,t))
				.collect(Collectors.toList())
				.toArray(new String[0]);
	}


	protected  void checkAutoAttrList(){
		if(autoAttrList==null){
			autoAttrList = new ArrayList<>(1);
		}
	}

	protected void checkAttrInsertIgnores(){
		if(attrInsertIgnores==null){
			attrInsertIgnores = new HashSet();
		}

	}

	protected  void checkAttrUpdateIgnores(){
		if(attrUpdateIgnores==null){
			attrUpdateIgnores = new HashSet<>();
		}
	}



}
