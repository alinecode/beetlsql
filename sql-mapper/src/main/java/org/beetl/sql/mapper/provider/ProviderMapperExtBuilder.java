package org.beetl.sql.mapper.provider;

import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.Select;
import org.beetl.sql.mapper.annotation.SqlProvider;
import org.beetl.sql.mapper.annotation.SqlTemplateProvider;
import org.beetl.sql.mapper.builder.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 解析mapper方法被{@link SqlTemplateProvider} 和 {@link SqlProvider} 注解，得出sql执行类
 * @author xiandafu
 * @see  org.beetl.sql.mapper.annotation.SqlTemplateProvider
 * @see org.beetl.sql.mapper.annotation.SqlProvider
 */
public class ProviderMapperExtBuilder implements MapperExtBuilder {
    @Override
    public MapperInvoke parse(Class entity, Method m) {
        SqlProvider sqlProvider =  m.getAnnotation(SqlProvider.class);
        ReturnTypeParser returnTypeParser = new ReturnTypeParser(m,entity);
        Annotation annotation  = MapperMethodParser.getSqlType(m);
        //参数说明
        ParameterParser parameterParser = new ParameterParser(m);
        MethodParamsHolder paramsHolder = parameterParser.getHolder();
        if(sqlProvider!=null){
            BaseSqlPMI mapperInvoke = null;
            if(paramsHolder.hasPageRequest()){
                Class targetType = returnTypeParser.getPageResultType();
                boolean pageResultRequired = false;
                if(PageResult.class.isAssignableFrom(returnTypeParser.getType())){
                    pageResultRequired  = true;
                }
                mapperInvoke = new SqlPagePMI(sqlProvider,targetType,pageResultRequired,paramsHolder);
            }else{
                boolean isSingle = !returnTypeParser.isCollection();
                boolean isSelect = annotation==null||annotation instanceof Select;
                Class targetType = isSelect?returnTypeParser.getCollectionType():returnTypeParser.getType();
                mapperInvoke = new SqlPMI(sqlProvider,targetType,isSelect,isSelect);
            }
            return mapperInvoke;

        }else{
            SqlTemplateProvider templateProvider = m.getAnnotation(SqlTemplateProvider.class);
            BaseSqlTemplatePMI mapperInvoke = null;
            if(paramsHolder.hasPageRequest()){
                Class targetType = returnTypeParser.getPageResultType();
                boolean pageResultRequired = false;
                if(PageResult.class.isAssignableFrom(returnTypeParser.getType())){
                    pageResultRequired  = true;
                }
                mapperInvoke = new SqlTemplatePagePMI(templateProvider,targetType,pageResultRequired,paramsHolder);
            }else {
                boolean isSelect = annotation == null || annotation instanceof Select;
                boolean isSingle = !returnTypeParser.isCollection();
                Class targetType = isSelect ? returnTypeParser.getCollectionType() : returnTypeParser.getType();
                 mapperInvoke =
                        new SqlTemplatePMI(templateProvider, targetType, paramsHolder, isSelect, isSingle);
                return mapperInvoke;
            }
            return mapperInvoke;
        }
    }
}
