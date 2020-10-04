package org.beetl.sql.mapper.provider;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.SqlTemplateProvider;
import org.beetl.sql.mapper.builder.MethodParam;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现{@code SqlTemplateProvider}
 * @author xiandafu
 */
public abstract  class BaseSqlTemplatePMI extends MapperInvoke {
    protected  SqlTemplateProvider sqlProvider = null;
    protected Class targetType = null;
    protected MethodParamsHolder holder;
    public BaseSqlTemplatePMI(SqlTemplateProvider sqlProvider, Class targetType, MethodParamsHolder holder){
        this.sqlProvider = sqlProvider;
        this.targetType = targetType;
        this.holder = holder;
    }

    public Object getParas(Object[] paras){
        return ParameterParser.wrapParasForSQLManager(paras,holder);
    }

    protected String getSQLTemplateByProvider(SqlTemplateProvider sqlProvider, Method owner, Object[] args) {
        Class<?> providerCls = null;
        String providerMethodName = null;
        try {
            providerCls = sqlProvider.provider();
            Object provider = BeanKit.newSingleInstance(providerCls);
            providerMethodName = sqlProvider.method();
            if (StringKit.isBlank(providerMethodName)) {
                providerMethodName = owner.getName();
            }

            Method providerMethod = providerCls.getMethod(providerMethodName, owner.getParameterTypes());
            if (providerMethod.getReturnType() != String.class) {
                throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "SqlTemplateProvider[" + providerCls.getName() + "]的方法[" + providerMethodName + "]返回值必须String类型");
            }
            providerMethod.setAccessible(Boolean.TRUE);
            String  sqlTemplate = (String) providerMethod.invoke(provider, args);
            return sqlTemplate;

        } catch (IllegalAccessException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "实例化" + providerCls.getName() + "失败，请检查是否有公有的无参构造");
        } catch (InvocationTargetException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "调用Provider方法出错" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "未能从" + providerCls.getName() + "获取到" + providerMethodName + " 方法");
        }
    }
}
