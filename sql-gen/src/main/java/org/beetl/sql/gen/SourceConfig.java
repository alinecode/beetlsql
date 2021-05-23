package org.beetl.sql.gen;

import lombok.Data;
import org.beetl.sql.clazz.ColDesc;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.CaseInsensitiveHashMap;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.gen.simple.EntitySourceBuilder;
import org.beetl.sql.gen.simple.MDSourceBuilder;
import org.beetl.sql.gen.simple.MapperSourceBuilder;

import java.util.*;

/**
 * 一个代码生成框架，用于beetlsql代码生成，也可以用于一般情况
 * <pre>{@code
 *  SourceConfig config = new SourceConfig(sqlManager);
 *  ConsoleOnlyProject project = new ConsoleOnlyProject();
 *  String tableName = "USER"
 *  config.gen(tableName,project)
 * }</pre>
 *
 * SourceConfig默认使用{@code sample}包下的例子EntitySourceBuilder，MapperSourceBuilder等作为代码生成并输出到控制台
 *
 * 也可以使用{@link org.beetl.sql.gen.simple.SimpleMavenProject} 输出到文件目录
 *
 * 用户可以自己扩展SourceConfig，BaseProject，以及 SourceBuilder 来完成自己的代码生成,比如
 * <pre>
 * config.getSourceBuilder().add(new MyControllerSourceBuilder());
 *
 * </pre>
 *
 *
 * @author xiandafu
 * @see org.beetl.sql.gen.simple.ConsoleOnlyProject
 * @see org.beetl.sql.gen.simple.SimpleMavenProject
 */

@Data
public class SourceConfig {


	/**
	 * 对应的日期类型
	 */
	public static enum PreferDateType{
		Date,Timestamp,LocalDate
	}

	/**
	 * 对应的浮点数生成类型
	 */
	public static enum PreferDoubleType{
		Double,BigDecimal
	}

	/**
	 * 配置好的代码生成器，默认有Entity,Mapper,MD
	 */
	protected List<SourceBuilder> sourceBuilder = new ArrayList<SourceBuilder>();

	/**
	 * 格式化代码，java模板代码要求按照4个空格写，生成代码最后会替换此变量的空格再输出
	 */
	protected int spaceCount = 4;
	// double 类型采用BigDecimal
	protected PreferDoubleType preferDoubleType = PreferDoubleType.BigDecimal;

	//采用java.util.Date 还是 java.util.Timestamp
	protected PreferDateType preferDateType = PreferDateType.Date;

	protected  boolean ignoreDbCatalog = true;

	/**
	 * 扩展属性，可以在SourceBuilder中使用
	 */
	protected Properties properties = new Properties();
	/**
	 * 输出
	 */
	private String encoding = "UTF-8";

	SQLManager sqlManager ;


	SourceBuilder entityBuilder = new EntitySourceBuilder();
	SourceBuilder mapperBuilder = new  MapperSourceBuilder();
	SourceBuilder mdBuilder = new  MDSourceBuilder();

	/***
	 * 使用BeetlSQL默认的的SourceBuilder,参考{@link #addDefault}
	 * @param sqlManager
	 *
	 */
	public SourceConfig(SQLManager sqlManager,boolean addDefault){
		this.sqlManager = sqlManager;
		if(addDefault){
			addDefault();
		}
	}

	public SourceConfig(SQLManager sqlManager,List<SourceBuilder> sourceBuilder) {
		this(sqlManager,false);
		this.sourceBuilder = sourceBuilder;

	}

	public SourceConfig(SQLManager sqlManager,List<SourceBuilder> sourceBuilder, PreferDoubleType preferDoubleType,
			PreferDateType preferDateType) {
		this(sqlManager,sourceBuilder);
		this.preferDoubleType = preferDoubleType;
		this.preferDateType = preferDateType;
	}

	/**
	 * BeetlSQL提供的默认生成器
	 * EntitySourceBuilder:生成实体
	 * MapperSourceBuilder 生成mapper
	 * MDSourceBuilder 生成md实例文件
	 */
	 public  void addDefault(){
		this.sourceBuilder.add(entityBuilder);
		this.sourceBuilder.add(mapperBuilder);
		this.sourceBuilder.add(mdBuilder);
	}

	public void removeEntityBuilder(){
		sourceBuilder.remove(entityBuilder);
	}

	public void removeMapperBuilder(){
		sourceBuilder.remove(mapperBuilder);
	}



	public void removeMdBuilder(){
		sourceBuilder.remove(mdBuilder);
	}

	public void addSourceBuilder(SourceBuilder builder){
		sourceBuilder.add(builder);
	}




	/**
	 * 生成指定表的所有Java代码
	 * @param tableName
	 * @param project
	 */
	public void gen(String tableName, BaseProject project){
		TableDesc tableDesc = sqlManager.getMetaDataManager().getTable(tableName);
		PackageList packageList = new PackageList();
		Entity entity = toEntity(tableDesc,packageList);
		entity.setTableDesc(tableDesc);
		entity.setImportPackage(packageList.getPkgs());
		for(SourceBuilder sourceBuilder :sourceBuilder){
			if(sourceBuilder.isSupport(this,entity)){
				sourceBuilder.generate(project,this,entity);
			}
		}
	}

	/**
	 * 生成工程所有代码
	 * @param project
	 * @param sourceFilter 过滤器
	 */
	public void genAll(BaseProject project, SourceFilter sourceFilter){
		MetadataManager metadataManager = sqlManager.getMetaDataManager();
		Set<String> tables = new TreeSet();
		tables.addAll(metadataManager.allTable());
		tables.forEach(tableName->{
			if(sourceFilter!=null){
				if(!sourceFilter.accept(metadataManager,tableName)){
					return ;
				}
			}
			gen(tableName,project);

		}
		);
	}

	/**
	 * 生成 {@code SQLManager} 包含的所有表和视图的代码
	 * @param project
	 */
	public void genAll(BaseProject project){
		this.genAll(project,null);
	}

	protected Entity toEntity(TableDesc tableDesc,PackageList packageList){
		Entity entity = new Entity();
		entity.setComment(tableDesc.getRemark());
		entity.setCatalog(tableDesc.getCatalog());
		entity.setTableName(tableDesc.getName());
		entity.setName(sqlManager.getNc().getClassName(tableDesc.getName()));

		ArrayList<Attribute> list = new ArrayList<>();
		CaseInsensitiveHashMap<String, ColDesc> cols =  tableDesc.getColsDetail();
		for(Map.Entry colInfo:cols.entrySet()){
			ColDesc colDesc = (ColDesc)colInfo.getValue();
			Attribute attribute = toAttribute(tableDesc,colDesc,packageList);
			list.add(attribute);
		}
		entity.setList(list);
		return entity;
	}

	protected  Attribute toAttribute(TableDesc tableDesc,ColDesc colDesc,PackageList packageList){
		Attribute attribute = new Attribute();
		attribute.setAuto(colDesc.isAuto());
		attribute.setColName(colDesc.getColName());
		attribute.setComment(colDesc.getRemark());
		String javaType = JavaType.mapping.get(colDesc.getSqlType());
		attribute.setJavaType(javaType);
		attribute.setName(sqlManager.getNc().getPropertyName(colDesc.getColName()));
		attribute.setJavaType(getJavaType(colDesc,packageList));
		if(tableDesc.getIdNames().contains(colDesc.getColName())){
			attribute.setId(true);
		}
		return attribute;
	}

	protected String getJavaType(ColDesc desc,PackageList packageList) {
		int jdbcType = desc.getSqlType();
		if (JavaType.isDateType(jdbcType)) {

			if (preferDateType == PreferDateType.Date) {
				packageList.getPkgs().add("java.util.Date");
				return "Date";
			} else if (preferDateType == PreferDateType.LocalDate) {
				boolean isTime = JavaType.isDateTimeType((jdbcType));
				if(isTime){
					packageList.getPkgs().add("java.times.LocalDateTime");
					return "LocalDateTime";
				}else{
					packageList.getPkgs().add("java.times.LocalDate");
					return "LocalDate";
				}

			} else {
				packageList.getPkgs().add("java.sql.Timestamp");
				return "Timestamp";
			}
		}


		String type = JavaType.getType(desc.getSqlType(), desc.getSize(), desc.getDigit());
		if (preferDoubleType==PreferDoubleType.BigDecimal&& type.equals("Double")) {
			packageList.getPkgs().add("java.math.BigDecimal");
			type = "BigDecimal";
		}

		return type;


	}
}
