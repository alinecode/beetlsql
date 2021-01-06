package org.beetl.sql.mapper.provider;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.annotation.SqlProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 实现{@code SqlProvider},mapper的sql语句由SqlProvider类来提供，
 * @author xiandafu
 */
public abstract  class BaseSqlPMI extends MapperInvoke {


    protected SQLReady getSQLReadyByProvider(SqlProvider sqlProvider, Method owner, Object[] args) {

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
            if (providerMethod.getReturnType() != SQLReady.class) {
                throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "SqlProvider类[" + providerCls.getName() + "]的方法[" + providerMethodName + "]返回值必须为SqlReady类型");
            }
            providerMethod.setAccessible(Boolean.TRUE);
            SQLReady sqlReady = (SQLReady) providerMethod.invoke(provider, args);
            return sqlReady;

        } catch (IllegalAccessException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "实例化" + providerCls.getName() + "失败，请检查是否有公有的无参构造");
        } catch (InvocationTargetException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "调用Provider方法出错" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new BeetlSQLException(BeetlSQLException.ANNOTATION_DEFINE_ERROR, "未能从" + providerCls.getName() + "获取到" + providerMethodName + " 方法");
        }
    }
}
