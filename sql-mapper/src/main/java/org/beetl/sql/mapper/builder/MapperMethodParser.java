package org.beetl.sql.mapper.builder;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.clazz.kit.*;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.*;
import org.beetl.sql.mapper.identity.BatchUpdateRMI;
import org.beetl.sql.mapper.identity.PageRMI;
import org.beetl.sql.mapper.identity.SelectRMI;
import org.beetl.sql.mapper.identity.UpdateRMI;
import org.beetl.sql.mapper.ready.BatchSqlReadyMI;
import org.beetl.sql.mapper.ready.PageSqlReadyMI;
import org.beetl.sql.mapper.ready.SelectSqlReadyMI;
import org.beetl.sql.mapper.ready.UpdateSqlReadyMI;
import org.beetl.sql.mapper.stream.StreamSqlIdMI;
import org.beetl.sql.mapper.stream.StreamSqlReadyMI;
import org.beetl.sql.mapper.stream.StreamTemplateSqlMI;
import org.beetl.sql.mapper.template.PageTemplateMI;
import org.beetl.sql.mapper.template.SelectTemplateMI;
import org.beetl.sql.mapper.template.UpdateTemplateMI;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 解析Mapper方法，得到MapperInvoke类,能解析内置的的Sql,TemplateSql,也支持通过Builder注解自定义注解
 * @author xiandafu
 */
@Plugin
public class MapperMethodParser {
    //mapper类的泛型类型
    protected  Class   defaultRetType;
    protected Method method = null;
    protected Class mapperClass = null;
    protected  int preferredSqlLen = -1;
    public MapperMethodParser(Class defaultRetType, Class mapperClass, Method method){
        this.defaultRetType = defaultRetType;
        this.mapperClass = mapperClass;
        this.method = method;
        this.preferredSqlLen = PropertiesKit.getInstance().getIntValue("MAPPER_SQL_MAX_LENGTH","-1");
    }

	/**
	 * 解析Mapper中定于定义方法，采用对应的MapperInvoke
	 * @return
	 */
	public MapperInvoke parse(){


        AutoMapper autoMapper = method.getAnnotation(AutoMapper.class);
        if(autoMapper!=null){
            MapperInvoke invoke = BeanKit.newSingleInstance(autoMapper.value());
            return invoke;
        }

        if(StreamData.class.isAssignableFrom(method.getReturnType())){
			MapperInvoke invoke =  parseStreamMethod();
			return invoke;
		}

        Sql sqlAnnotation = method.getAnnotation(Sql.class);
        if(sqlAnnotation!=null){
			MapperInvoke invoke = parseSqlMethod(sqlAnnotation);
			return invoke;
        }

        Template templateAnnotation = method.getAnnotation(Template.class);
        if(templateAnnotation !=null){
			MapperInvoke invoke =  parseSqlTemplateMethod(templateAnnotation);
			return invoke;
        }

        MapperExtBuilder mapperBuilder = findExtBuilder();
        if(mapperBuilder!=null){
			MapperInvoke invoke =  mapperBuilder.parse(defaultRetType,method);
			return invoke;
        }

        //默认，sqlId方式
		MapperInvoke invoke =  parseSqlId();
        return invoke;

    }

    protected String getNamespace(){
        SqlResource methodSqlResoruce = method.getAnnotation(SqlResource.class);
        if(methodSqlResoruce!=null){
            return methodSqlResoruce.value();
        }
        SqlResource sqlResource =  (SqlResource)mapperClass.getAnnotation(SqlResource.class);
        if(sqlResource!=null){
           return sqlResource.value();
        }
        //从实体名获取
        if(defaultRetType!=null){
            String namespace = StringKit.toLowerCaseFirstOne(defaultRetType.getSimpleName());
            return namespace;
        }
        throw new BeetlSQLException(BeetlSQLException.MAPPER_ERROR,"需要使用@SqlResource");
    }

    protected MapperInvoke parseSqlId(){
        //TODO 咋搞
        String namespace = getNamespace();
        String id = method.getName();
        SqlId sqlId = SqlId.of(namespace,id);
        Annotation action = getSqlType(method);
        //返回值说明
        ReturnTypeParser returnTypeParser = new ReturnTypeParser(method,defaultRetType);
        //参数说明
        ParameterParser parameterParser = new ParameterParser(method);
        MethodParamsHolder paramsHolder = parameterParser.getHolder();
        if(action==null||action instanceof Select){
            if(paramsHolder.hasPageRequest()){
                Class targetRetType = returnTypeParser.getPageResultType();
                if(returnTypeParser.isPageResult()) {
                    return new PageRMI(sqlId,targetRetType,true,paramsHolder);
                }else{
                    return new PageRMI(sqlId,targetRetType,false,paramsHolder);
                }

            }else{
                if(returnTypeParser.isCollection()){
                    Class targetRetType = returnTypeParser.getCollectionType();
                    return new SelectRMI(sqlId,targetRetType,paramsHolder,false);
                }else{
                    Class targetRetType = returnTypeParser.getType();
                    return new SelectRMI(sqlId,targetRetType,paramsHolder,true);
                }
            }
        }else if(action instanceof  Update){
            return new UpdateRMI(sqlId,paramsHolder);
        }else if(action instanceof  BatchUpdate){
            return new BatchUpdateRMI(sqlId,paramsHolder);
        }else{
            throw new UnsupportedOperationException();
        }

    }


    /**
     * 找到扩展的注解解释器,包含
     * <ul>
     *     <li>SpringData</li>
     *     <li>SqlProvider</li>
     *     <li>SqlTemplateProvider</li>
     * </ul>
     * @return
     */
    protected  MapperExtBuilder findExtBuilder(){
        Annotation[] ans = method.getAnnotations();
        for(Annotation an:ans){
            Builder builder = an.annotationType().getAnnotation(Builder.class);
            if(builder==null){
                continue;
            }
            Class c = builder.value();
            Object o =   BeanKit.newSingleInstance(c);
            if(!(o instanceof MapperExtBuilder) ){
                throw new IllegalStateException("期望 "+ MapperExtBuilder.class+" 但是 "+c);
            }
            return (MapperExtBuilder)o;
        }
        return null;
    }



    protected MapperInvoke parseSqlTemplateMethod(Template sqlAnnotation){
        String sqlTemplate = sqlAnnotation.value();
		checkSqlLength(sqlTemplate);
        Annotation action = getSqlType(method);
        //返回值说明
        ReturnTypeParser returnTypeParser = new ReturnTypeParser(method,defaultRetType);
        //参数说明
        ParameterParser parameterParser = new ParameterParser(method);
        MethodParamsHolder paramsHolder = parameterParser.getHolder();

        if(action==null||action instanceof Select){
            if(paramsHolder.hasPageRequest()){
                Class targetRetType = returnTypeParser.getPageResultType();
                if(returnTypeParser.isPageResult()) {
                    return new PageTemplateMI(sqlTemplate,targetRetType,true,paramsHolder);
                }else{
                    return new PageTemplateMI(sqlTemplate,targetRetType,false,paramsHolder);
                }

            }else{
                if(returnTypeParser.isCollection()){
                    Class targetRetType = returnTypeParser.getCollectionType();
                    return new SelectTemplateMI(sqlTemplate,targetRetType,paramsHolder,false);
                }else{
                    Class targetRetType = returnTypeParser.getType();
                    return new SelectTemplateMI(sqlTemplate,targetRetType,paramsHolder,true);
                }
            }
        }else if(action instanceof  Update){
            return new UpdateTemplateMI(sqlTemplate,paramsHolder);
        }else{
            throw new UnsupportedOperationException("不支持 "+action.toString());
        }
    }

    protected  MapperInvoke parseStreamMethod(){
    	Sql sqlAnnotation = this.method.getAnnotation(Sql.class);
		Class targetType = getStreamType();

		ParameterParser parameterParser = new ParameterParser(method);
		MethodParamsHolder paramsHolder = parameterParser.getHolder();

    	if(sqlAnnotation!=null){
			StreamSqlReadyMI sqlReadyMI = new StreamSqlReadyMI(sqlAnnotation.value(),targetType);
			return sqlReadyMI;
		}

    	Template templateAnnotation = this.method.getAnnotation(Template.class);
    	if(templateAnnotation!=null){
    		String sqlTemplate = templateAnnotation.value();
			StreamTemplateSqlMI streamTemplateSqlMI = new StreamTemplateSqlMI(sqlTemplate,targetType,paramsHolder);
			return streamTemplateSqlMI;
		}

		String namespace = getNamespace();
		String id = method.getName();
		SqlId sqlId = SqlId.of(namespace,id);

		StreamSqlIdMI sqlIdMI = new StreamSqlIdMI(sqlId,targetType,paramsHolder);
		return  sqlIdMI;

	}

	protected  Class getStreamType(){
    	Type t = method.getGenericReturnType();
		if(!(t instanceof ParameterizedType) ){
			return defaultRetType;
		}
		Class type = BeanKit.getParameterTypeClass(method.getGenericReturnType());
		return type!=null?type:this.defaultRetType;
	}

    protected MapperInvoke parseSqlMethod(Sql sqlAnnotation){
        String jdbcSql = sqlAnnotation.value();
		checkSqlLength(jdbcSql);
        Annotation action = getSqlType(method);
        ReturnTypeParser returnTypeParser = new ReturnTypeParser(method,defaultRetType);
        //参数说明
        ParameterParser parameterParser = new ParameterParser(method);
        MethodParamsHolder paramsHolder = parameterParser.getHolder();

        if(action==null||action instanceof Select){
            if(paramsHolder.hasPageRequest()){
                if(returnTypeParser.isPageResult()) {
                    Class targetRetType = returnTypeParser.getPageResultType();
                    return new PageSqlReadyMI(jdbcSql,targetRetType,true,paramsHolder);
                }else{
                    Class targetRetType = returnTypeParser.getPageResultType();
                    return new PageSqlReadyMI(jdbcSql,targetRetType,false,paramsHolder);
                }
            }
            else{
                if(returnTypeParser.isCollection()){
                    Class targetRetType = returnTypeParser.getCollectionType();
                    return new SelectSqlReadyMI(jdbcSql,targetRetType,false);
                }
                else{
                    return new SelectSqlReadyMI(jdbcSql,returnTypeParser.target,true);
                }
            }
        }else if(action instanceof  Update){
            return new UpdateSqlReadyMI(jdbcSql);
        }else if(action instanceof BatchUpdate){
            return new BatchSqlReadyMI(jdbcSql);
        }else{
            throw new UnsupportedOperationException();
        }
    }



    public static  Annotation getSqlType(Method method){
        Select select = method.getAnnotation(Select.class);
        if(select!=null){
            return select;
        }
        Update update = method.getAnnotation(Update.class);
        if(update!=null){
            return update;
        }
        BatchUpdate batchUpdate = method.getAnnotation(BatchUpdate.class);
        if(batchUpdate!=null){
            return batchUpdate;
        }

        return null;
    }


    protected Class getMappingEntity(){
        return method.getReturnType();
    }

	protected  void checkSqlLength(String sql){
    	if(preferredSqlLen==-1){
    		return;
		}
    	if(sql.length()>preferredSqlLen){
    		throw new BeetlSQLException(BeetlSQLException.MAPPER_SQL_LIMIT,"期望Mapper方法 "+method+" 的SQL最大长度是 "+preferredSqlLen+" 实际是 "+sql.length());
		}
	}



}
