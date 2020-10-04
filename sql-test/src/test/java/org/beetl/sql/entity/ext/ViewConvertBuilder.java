package org.beetl.sql.entity.ext;

import org.beetl.sql.annotation.builder.BeanConvert;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;

public class ViewConvertBuilder implements BeanConvert {

    @Override
    public Object before(ExecuteContext ctx, Object obj, Annotation an) {
       return obj;
    }

    @Override
    public Object after(ExecuteContext ctx, Object obj, Annotation an) {
        return obj;
    }
}
