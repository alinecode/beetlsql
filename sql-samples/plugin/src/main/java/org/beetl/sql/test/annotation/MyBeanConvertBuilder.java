package org.beetl.sql.test.annotation;

import org.beetl.sql.annotation.builder.BeanConvert;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.test.PluginAnnotationSample;

import java.lang.annotation.Annotation;

/**
 * 演示从数据库取出后，在做一些操作
 * @see LoadOne
 * @author xiandafu
 */
public class MyBeanConvertBuilder implements BeanConvert {
    @Override
    public  Object after(ExecuteContext ctx, Object obj, Annotation an){
        LoadOne loadOne = (LoadOne)an;
        System.out.println("paras "+ loadOne.name());
        PluginAnnotationSample.UserDetail detail =(PluginAnnotationSample.UserDetail)obj;
        Integer deptId = detail.getDepartmentId();
        PluginAnnotationSample.DepartmentInfo deptInfo = ctx.sqlManager.single(PluginAnnotationSample.DepartmentInfo.class,deptId);
        detail.setDept(deptInfo);
        return obj;
    }
}
